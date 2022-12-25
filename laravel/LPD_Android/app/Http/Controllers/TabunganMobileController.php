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

class TabunganMobileController extends Controller
{
    public function index(Request $request,$token){
        // $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        // $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
        //     ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        $bukuTabungan=BukuTabungan::latest()->where('nasabah_id',$request->get('login_user')->id)->with('nasabah.kolektor')->firstOrFail();
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
        $transaksis=Transaksi::latest()->whereHas('bukutabungan',function($q) use($request) { 
            $q->whereHas('nasabah',function($x) use($request) { 
                $x->where('id',$request->get('login_user')->id);
            });
        })->with('bukutabungan.nasabah.kolektor')->get();
        return $transaksis;
    }

    public function indexStaff(Request $request,$token,Nasabah $nasabah){
        if($nasabah->staff_id!=$request->get('login_user')->id){
            return response()->json(['message' => 'Forbidden'], 403);
        }

        $bukuTabungan=BukuTabungan::latest()->where('nasabah_id',$nasabah->id)->with('nasabah.kolektor')->firstOrFail();
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
        $transaksis=Transaksi::latest()->whereHas('bukutabungan',function($q) use($request,$nasabah) { 
            $q->whereHas('nasabah',function($x) use($request,$nasabah) { 
                $x->where('id',$nasabah->id);
            });
        })->with('bukutabungan.nasabah.kolektor')->get();
        return $transaksis;
    }
}
