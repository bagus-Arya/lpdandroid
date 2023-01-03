<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use \App\Models\Token;
use \App\Models\Nasabah;
use \App\Models\BukuTabungan;
use \App\Models\Transaksi;
use \App\Models\Staff;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;


class PenarikanMobileController extends Controller
{

    public function index(Request $request,$token){
        if($request->get('login_user')->role=="Bendahara"){
            return Transaksi::where('type_transaksi','Penarikan')->where('status','unvalidated')->with('bukutabungan.nasabah.kolektor')->get();
        }
        elseif($request->get('login_user')->role=="Kolektor"){
            return Transaksi::whereHas('bukutabungan',function($q) use($request){
                $q->whereHas('nasabah',function($s) use($request){
                    $s->where('staff_id',$request->get('login_user')->id);
                });
            })->where('type_transaksi','Penarikan')->where('status','validated-bendahara')->with('bukutabungan.nasabah.kolektor')->get();
        }
        elseif($request->get('login_user')->role=="Nasabah"){
            return Transaksi::whereHas('bukutabungan',function($q) use($request){
                $q->whereHas('nasabah',function($s) use($request){
                    $s->where('id',$request->get('login_user')->id);
                });
            })->where('type_transaksi','Penarikan')->where('status','validated-kolektor')->with('bukutabungan.nasabah.kolektor')->get();
        }
        else{
            return response()->json(['message' => 'No content'], 200);
        }
    }

    public function store(Request $request,$token){
        $validate = $request->validate([
            'nominal'=>'required|integer',
            'tgl_transaksi'=>'required|date_format:Y-m-d',
        ]);
        $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        $earliestDate=new Carbon(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)->min('tgl_transaksi'));
        $latestDate=new Carbon(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)->max('tgl_transaksi'));
        $diffInYears = $earliestDate->diffInYears($latestDate);
        $currentDate=$earliestDate;
        $currentSaldo=0;
        if($diffInYears!=0){
            for ($i = 1; $i <= $diffInYears; $i++) {
                $totalSetoranThisYear=Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                                    ->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                                    ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                    ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                    ->sum('nominal');
                $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                                    ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')
                                    ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                    ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                    ->sum('nominal');
                
                $totalSaldoThisYear=$currentSaldo+$totalSetoranThisYear-$totalPenarikanThisYear;
                $currentSaldo=$totalSaldoThisYear+((4/100) * $totalSaldoThisYear);
                if($i==$diffInYears){
                    $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                    ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                    ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                }
                $currentDate=$currentDate->copy()->addYear();
            }
            $nasabah['saldo']=$currentSaldo;
        }else{
            $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        }
        if($nasabah['saldo']<$validate['nominal']){
            return response()->json(['message' => 'Conflicted With Current State Of Resource'], 409);
        }

        $validate['type_transaksi']="Penarikan";
        $validate['status']="unvalidated";
        $validate['buku_tabungan_id']=BukuTabungan::Where('nasabah_id',$request->get('login_user')->id)->first()->id;
        return Transaksi::create($validate);
    }

    public function updateValidasiBendahara(Request $request,$token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="unvalidated"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'validated-bendahara',
                'tgl_validasi_bendahara'=>$todayDate,
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function updateRejectBendahara(Request $request,$token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="unvalidated"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'rejected-bendahara',
                'tgl_validasi_bendahara'=>$todayDate
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function updateValidasiKolektor(Request $request,$token,Transaksi $transaksi){
        // return response()->json(['message' => 'Forbiden'], 403);
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-bendahara"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'validated-kolektor',
                'tgl_validasi_kolektor'=>$todayDate,
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function updateRejectKolektor(Request $request,$token,Transaksi $transaksi){
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-bendahara"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'rejected-kolektor',
                'tgl_validasi_kolektor'=>$todayDate,
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function updateValidasiNasabah(Request $request,$token,Transaksi $transaksi){
        // return response()->json(['message' => 'yolo'], 400);
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-kolektor"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'validated-nasabah',
                'tgl_validasi_nasabah'=>$todayDate,
            ]);
            return response()->json(['message' => 'change successxxxx'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function updateRejectNasabah(Request $request,$token,Transaksi $transaksi){
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-kolektor"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'rejected-nasabah',
                'tgl_validasi_nasabah'=>$todayDate,
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function penarikan(Request $request, $token){
        $todayDate = Carbon::now()->format('Y-m-d');
        
        $data = Transaksi::where('type_transaksi','Penarikan')
        ->with('bukutabungan.nasabah.kolektor')
        ->get();

        $SumDay = Transaksi::where('type_transaksi','Penarikan')
        ->where('tgl_transaksi',$todayDate)
        ->with('bukutabungan.nasabah.kolektor')
        ->sum('nominal');

        return view('penarikan',compact('data','SumDay'));
    }
}
