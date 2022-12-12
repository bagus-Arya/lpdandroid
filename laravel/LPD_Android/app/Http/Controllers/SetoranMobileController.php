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

class SetoranMobileController extends Controller
{
    
    public function index(Request $request,Token $token){
        if($request->get('login_user')->role=="Bendahara"){
            return Transaksi::where('type_transaksi','Setoran')->where('status','unvalidated')->with('bukutabungan.nasabah.kolektor')->get();
        }
        return response()->json(['message' => 'No content'], 204);
    }

    public function store(Request $request,Token $token,Nasabah $nasabah){
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

    public function updateValidasiBendahara(Request $request,Token $token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Setoran" && $transaksi->status=="unvalidated"){
            return $transaksi->update([
                'status'=>'validated-bendahara'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }

    public function updateRejectBendahara(Request $request,Token $token,Transaksi $transaksi){
        if($transaksi->type_transaksi=="Setoran" && $transaksi->status=="unvalidated"){
            return $transaksi->update([
                'status'=>'rejected-bendahara'
            ]);
        }
        return response()->json(['message' => 'Unchanged'], 304);
    }
}
