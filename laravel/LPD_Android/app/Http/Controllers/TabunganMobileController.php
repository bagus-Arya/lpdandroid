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
use Carbon\Carbon;

class TabunganMobileController extends Controller
{
    public function index(Request $request,$token){
        $bukuTabungan=BukuTabungan::where('nasabah_id',$request->get('login_user')->id)->with('nasabah.kolektor',function ($q){
            $q->withTrashed();
        })->firstOrFail();
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
            $bukuTabungan['saldo']=$currentSaldo;
            return $bukuTabungan; 
        }
        $bukuTabungan['saldo']=(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$request->get('login_user')->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        return $bukuTabungan; 
    }
    public function transaksis(Request $request,$token){
        // $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        // $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        $transaksis=Transaksi::latest('tgl_transaksi')->whereHas('bukutabungan',function($q) use($request) { 
            $q->whereHas('nasabah',function($x) use($request) { 
                $x->where('id',$request->get('login_user')->id);
            });
        })->with('bukutabungan.nasabah.kolektor',function ($q){
            $q->withTrashed();
        })->get();
        return $transaksis;
    }

    public function indexStaff(Request $request,$token,Nasabah $nasabah){
        if($nasabah->staff_id!=$request->get('login_user')->id){
            return response()->json(['message' => 'Forbidden'], 403);
        }

        $bukuTabungan=BukuTabungan::where('nasabah_id',$nasabah->id)->with('nasabah.kolektor')->firstOrFail();
        $earliestDate=new Carbon(Transaksi::where('buku_tabungan_id',$nasabah->id)->min('tgl_transaksi'));
        $latestDate=new Carbon(Transaksi::where('buku_tabungan_id',$nasabah->id)->max('tgl_transaksi'));
        $diffInYears = $earliestDate->diffInYears($latestDate);
        $currentDate=$earliestDate;
        $currentSaldo=0;
        if($diffInYears!=0){
            for ($i = 1; $i <= $diffInYears; $i++) {
                $totalSetoranThisYear=Transaksi::where('buku_tabungan_id',$nasabah->id)
                                    ->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                                    ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                    ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                    ->sum('nominal');
                $totalPenarikanThisYear=Transaksi::where('buku_tabungan_id',$nasabah->id)
                                    ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')
                                    ->whereDate('tgl_transaksi', '>=', $currentDate->copy()->subYear()->addDay()->format('Y-m-d'))
                                    ->whereDate('tgl_transaksi', '<=', $currentDate->copy()->format('Y-m-d'))
                                    ->sum('nominal');
                
                $totalSaldoThisYear=$currentSaldo+$totalSetoranThisYear-$totalPenarikanThisYear;
                $currentSaldo=$totalSaldoThisYear+((4/100) * $totalSaldoThisYear);
                if($i==$diffInYears){
                    $currentSaldo=$currentSaldo+(Transaksi::where('buku_tabungan_id',$nasabah->id)
                    ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->id)
                    ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->whereDate('tgl_transaksi', '>=', $currentDate->copy()->addDay()->format('Y-m-d'))->sum('nominal')); 
                }
                $currentDate=$currentDate->copy()->addYear();
            }
            $bukuTabungan['saldo']=$currentSaldo;
            return $bukuTabungan; 
        }
        $bukuTabungan['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        return $bukuTabungan; 
    }

    public function transaksisStaff(Request $request,$token,Nasabah $nasabah){
        if($nasabah->staff_id!=$request->get('login_user')->id){
            return response()->json(['message' => 'Forbidden'], 403);
        }
        // $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        // $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        $transaksis=Transaksi::latest('tgl_transaksi')->whereHas('bukutabungan',function($q) use($request,$nasabah) { 
            $q->whereHas('nasabah',function($x) use($request,$nasabah) { 
                $x->where('id',$nasabah->id);
            });
        })->with('bukutabungan.nasabah.kolektor')->get();
        return $transaksis;
    }
}
