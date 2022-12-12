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
    public function index(Request $request,Token $token){
        $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        return $nasabah;
    }
}
