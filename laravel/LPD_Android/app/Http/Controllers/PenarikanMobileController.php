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


class PenarikanMobileController extends Controller
{

    public function index(Request $request,Token $token){
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
        else{
            return response()->json(['message' => 'No content'], 204);
        }
    }

    public function store(Request $request,Token $token){
        $validate = $request->validate([
            'nominal'=>'required|integer',
            'tgl_transaksi'=>'required|date_format:Y-m-d',
        ]);
        $nasabah=Nasabah::where('id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        
        if($nasabah['saldo']<$validate['nominal']){
            return response()->json(['message' => 'Conflicted With Current State Of Resource'], 409);
        }

        $validate['type_transaksi']="Penarikan";
        $validate['status']="unvalidated";
        $validate['buku_tabungan_id']=BukuTabungan::Where('nasabah_id',$request->get('login_user')->id)->first()->id;
        return Transaksi::create($validate);
    }

    public function updateValidasiBendahara(Request $request,Token $token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="unvalidated"){
            return $transaksi->update([
                'status'=>'validated-bendahara'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }

    public function updateRejectBendahara(Request $request,Token $token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="unvalidated"){
            return $transaksi->update([
                'status'=>'rejected-bendahara'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }

    public function updateValidasiKolektor(Request $request,Token $token,Transaksi $transaksi){
        
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-bendahara"){
            return $transaksi->update([
                'status'=>'validated-kolektor'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }

    public function updateRejectKolektor(Request $request,Token $token,Transaksi $transaksi){
        if($request->get('login_user')->id!=$transaksi->bukutabungan->nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        if($transaksi->type_transaksi=="Penarikan" && $transaksi->status=="validated-bendahara"){
            return $transaksi->update([
                'status'=>'rejected-kolektor'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }
}
