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

class SetoranMobileController extends Controller
{
    
    public function index(Request $request,$token){
        if($request->get('login_user')->role=="Bendahara"){
            $transaksis=Transaksi::where('type_transaksi','Setoran')->where('status','unvalidated')->with('bukutabungan.nasabah.kolektor',function ($q){
                $q->withTrashed();
            })->get();
            foreach ($transaksis as $transaksi){ 
                $earliestDate=new Carbon(Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)->min('tgl_transaksi'));
                $latestDate=new Carbon(Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)->max('tgl_transaksi'));
                $diffInYears = $earliestDate->diffInYears($latestDate);
                $currentDate=$earliestDate;
                $currentSaldo=0;
                if($diffInYears!=0){
                    for ($i = 1; $i <= $diffInYears; $i++) {
                        $totalSetoranThisYear=Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)
                                            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                                            ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                            ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                            ->sum('nominal');
                        // $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                        //                     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')
                        //                     ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                        //                     ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                        //                     ->sum('nominal');
        
                        $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)
                                            ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')
                                            ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                            ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                            ->sum('nominal');
                        
                        $totalSaldoThisYear=$currentSaldo+$totalSetoranThisYear-$totalPenarikanThisYear;
                        $currentSaldo=$totalSaldoThisYear+((4/100) * $totalSaldoThisYear);
                        if($i==$diffInYears){
                            // $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                            // ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                            // ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                            
                            $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)
                            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                            ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                        }
                        $currentDate=$currentDate->copy()->addYear();
                    }
                    $transaksi['saldo']=$currentSaldo;
                }
                else{
                    $transaksi['saldo']=(Transaksi::where('buku_tabungan_id',$transaksi->bukutabungan->nasabah->id)
                    ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                    ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')->sum('nominal'));
                }
            }
            return $transaksis;
        }
        return response()->json(['message' => 'No content'], 204);
    }

    public function store(Request $request,$token,Nasabah $nasabah){
        // return $request->get('login_user');
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        $validate = $request->validate([
            'nominal'=>'required|integer',
            'tgl_transaksi'=>'required|date_format:Y-m-d',
        ]);
        $validate['type_transaksi']="Setoran";
        $validate['status']="unvalidated";
        $validate['buku_tabungan_id']=BukuTabungan::Where('nasabah_id',$nasabah->id)->first()->id;
        return Transaksi::create($validate);
    }

    public function updateValidasiBendahara(Request $request,$token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Setoran" && $transaksi->status=="unvalidated"){
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
        if($transaksi->type_transaksi=="Setoran" && $transaksi->status=="unvalidated"){
            $todayDate = Carbon::now()->format('Y-m-d');
            $transaksi->update([
                'status'=>'rejected-bendahara',
                'tgl_validasi_bendahara'=>$todayDate,
            ]);
            return response()->json(['message' => 'change success'], 200);
        }
        return response()->json(['message' => 'Unchanged'], 400);
    }

    public function setoran(Request $request, $token){
        $todayDate = Carbon::now()->format('Y-m-d');
        
        $data = Transaksi::where('type_transaksi','Setoran')
        ->with('bukutabungan.nasabah.kolektor')
        ->get();

        $SumDay = Transaksi::where('type_transaksi','Setoran')
        ->where('tgl_transaksi',$todayDate)
        ->with('bukutabungan.nasabah.kolektor')
        ->sum('nominal');

        return view('setoran',compact('data','SumDay'));
    }
}
