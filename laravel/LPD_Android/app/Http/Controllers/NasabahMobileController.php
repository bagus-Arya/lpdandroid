<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use \App\Models\Token;
use \App\Models\Nasabah;
use \App\Models\BukuTabungan;
use \App\Models\Transaksi;
use \App\Models\Staff;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class NasabahMobileController extends Controller
{
    public function index(Request $request,$token){
        if($request->get('login_user')->role=="Bendahara" || $request->get('login_user')->role=="Ketua"){
            $filters=$request->validate([
                'fullname'=>'string',
                'staff_id'=>'integer'
            ]);
            if (array_key_exists('staff_id',$filters)){
                
            }
            else{
                $filters['staff_id']=0;
            }
            // return BukuTabungan::filter($filters)->get();
            $nasabahs=Nasabah::latest()->where(function($q) use($request,$filters) {
                $q->where('staff_id',$filters['staff_id'] ? $filters['staff_id']:0)
                    ->whereHas('bukutabungan',function($q) use ($filters) { 
                        $q->filter($filters);
                    });
            })->orWhere(function($q) use($request,$filters){
                $q->where('staff_id',$filters['staff_id'] ? $filters['staff_id']:0)->filter($filters);
            })->with('kolektor',function ($q){
                $q->withTrashed();
            })->with('bukutabungan')->get()->makeVisible(['password']);
        }
        elseif($request->get('login_user')->role=="Kolektor"){
            $filters=$request->validate([
                'fullname'=>'string',
            ]);
            // return BukuTabungan::filter($filters)->get();
            $nasabahs=Nasabah::latest()->where(function($q) use($request,$filters) {
                $q->where('staff_id',$request->get('login_user')->id)
                    ->whereHas('bukutabungan',function($q) use ($filters) { 
                        $q->filter($filters);
                    });
            })->orWhere(function($q) use($request,$filters){
                $q->where('staff_id',$request->get('login_user')->id)->filter($filters);
            })->with('kolektor',function ($q){
                $q->withTrashed();
            })->with('bukutabungan')->get()->makeVisible(['password']);
        }
       
        foreach ($nasabahs as $nasabah) {
            // $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            // ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            // ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
            $bukuTabungan=BukuTabungan::where('nasabah_id',$nasabah->id)->with('nasabah.kolektor',function ($q){
                $q->withTrashed();
            })->firstOrFail();
            $earliestDate=new Carbon(Transaksi::where('buku_tabungan_id',$nasabah->id)->min('tgl_transaksi'));
            $latestDate=new Carbon(Transaksi::where('buku_tabungan_id',$nasabah->id)->max('tgl_transaksi'));
            $diffInYears = $earliestDate->diffInYears($latestDate);
            $currentDate=$earliestDate;
            $currentSaldo=0;
            $currentBunga=0;
            if($diffInYears!=0){
                for ($i = 1; $i <= $diffInYears; $i++) {
                    // return response()->json(['message1' =>  $currentDate->copy()->subYear()->format('Y-m-d'),
                    // 'message2' =>   $currentDate->copy()->format('Y-m-d')], 403);
                    $totalSetoranThisYear=Transaksi::where('buku_tabungan_id',$nasabah->id)
                                        ->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                                        ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->format('Y-m-d'))
                                        ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->addYear()->format('Y-m-d') )
                                        // ->whereBetween('tgl_transaksi', [$from, $to])
                                        ->sum('nominal');
                   
                    
                    // $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                    //                     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')
                    //                     ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                    //                     ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                    //                     ->sum('nominal');
    
                    $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$nasabah->id)
                                        ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')
                                        ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->format('Y-m-d'))
                                        ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->addYear()->format('Y-m-d') )
                                        ->sum('nominal');
    
                    
                    $totalSaldoThisYear=$currentSaldo+$totalSetoranThisYear-$totalPenarikanThisYear;
                    $currentBunga=$currentBunga+((4* $totalSaldoThisYear)/100);
                    $currentSaldo=$totalSaldoThisYear+((4* $totalSaldoThisYear)/100);
                    
                    $currentDate=$currentDate->copy()->addYear();
                    if($i==$diffInYears){
                        // if($i==1){
                        //     return response()->json(
                        //         [
                        //          'message1' =>  $currentDate->copy()->format('Y-m-d'),
                        //          'message2' =>   $currentDate->copy()->addYear()->format('Y-m-d') ,
                        //          'message3'=> $totalSetoranThisYear,
                        //          'message4'=> $totalPenarikanThisYear,
                        //          'message5'=> $currentSaldo
                        //         ], 403);
                        // }
                        // $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                        // ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                        // ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                        
                        $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$nasabah->id)
                        ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->id)
                        ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                    }
                    
                }
                $nasabah['saldo']=$currentSaldo;
                // return $bukuTabungan; 
            }
            else{
                // $bukuTabungan['saldo']=(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                //     ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
                //     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        
                $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->id)
                ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->id)
                ->where('type_transaksi','Penarikan')->where('status','validated-nasabah')->sum('nominal'));
                // return $bukuTabungan; 
            }
           
        }
        
        return $nasabahs;
        // return Nasabah::latest()->filter($filters)->get()->except($request->get('login_user')->id);
    }

    public function show(Request $request,$token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        $nasabah=Nasabah::where('id',$nasabah->id)->where('staff_id',$request->get('login_user')->id)->with('kolektor',function ($q){
            $q->withTrashed();
        })->with('bukutabungan')->firstOrFail()->makeVisible(['password']);
        $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        return $nasabah;
    }

    public function store(Request $request,$token){
        // validate input
        $validate = $request->validate([
            'fullname'=>'required|string',
            'alamat'=>'required|string',
            'username'=>'required|unique:nasabahs|unique:staff',
            'no_telepon'=>'required|string',
            'no_ktp'=>'required|string',
            'tgl_lahir'=>'required|date_format:Y-m-d',
            'ktp_photo'=>'required|string',
            'password'=>'required|string',
            'jenis_kelamin'=>[
                'required',
                'string',
                Rule::in(['Laki-Laki','Perempuan'])
            ]
        ]);
       
        // membuat banjar dan device kulkul baru
        return DB::transaction(function () use ($request,$validate){
            try {
                $validate['staff_id']=$request->get('login_user')->id;
                $newNasabah=Nasabah::create($validate);
                BukuTabungan::create([
                    'no_tabungan'=>(BukuTabungan::max('id')+1).'-'.rand(100000,999999),
                    'nasabah_id'=>$newNasabah->id,
                ]);
                DB::commit();
                return $newNasabah;
            } catch (\Throwable $th) {
                DB::rollback();
                return response()->json(['message' => $th], 500);
            }
        });
    }
    public function destroy(Request $request,$token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => $nasabah], 403);
        }
        Token::where('type','Nasabah')->where('user_id',$nasabah->id)->delete();
        $nasabah->delete();
        return response()->json(['message' => 'nasabah deleted'], 200);

    }

    public function update(Request $request,$token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        $validate = $request->validate([
            'fullname'=>'required|string',
            'alamat'=>'required|string',
            'no_telepon'=>'required|string',
            'no_ktp'=>'required|string',
            'tgl_lahir'=>'required|date_format:Y-m-d',
            'ktp_photo'=>'nullable|string',
            'password'=>'required|string',
            'jenis_kelamin'=>[
                'required',
                'string',
                Rule::in(['Laki-Laki','Perempuan'])
            ]
        ]);
        $nasabah->update($validate);
        return Nasabah::where('id',$nasabah->id)->firstOrFail();
    }
}
