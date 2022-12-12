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

class TransaksiMobileController extends Controller
{
    public function index(Request $request,Token $token){
        if($request->get('login_user')->role=="Bendahara"){
            return Transaksi::with('bukutabungan.nasabah.kolektor')->get();
        }
        elseif($request->get('login_user')->role=="Kolektor"){
            return Transaksi::whereHas('bukutabungan',function($q) use($request){
                $q->whereHas('nasabah',function($s) use($request){
                    $s->where('staff_id',$request->get('login_user')->id);
                });
            })->with('bukutabungan.nasabah.kolektor')->get();
        }
        else{
            return response()->json(['message' => 'No content'], 204);
        }
    }
}
