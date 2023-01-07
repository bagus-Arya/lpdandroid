<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use \App\Models\Token;
use \App\Models\Nasabah;
use \App\Models\BukuTabungan;
use \App\Models\Transaksi;
use \App\Models\Staff;
use DB;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;

class TransaksiMobileController extends Controller
{
    public function index(Request $request,$token){
        if($request->get('login_user')->role=="Bendahara"){
            return Transaksi::latest('tgl_transaksi')->with('bukutabungan.nasabah.kolektor',function ($q){
                $q->withTrashed();
            })->get();
        }
        elseif($request->get('login_user')->role=="Kolektor"){
            return Transaksi::latest('tgl_transaksi')->whereHas('bukutabungan',function($q) use($request){
                $q->whereHas('nasabah',function($s) use($request){
                    $s->where('staff_id',$request->get('login_user')->id);
                });
            })->with('bukutabungan.nasabah.kolektor',function ($q){
                $q->withTrashed();
            })->get();
        }
        else{
            return response()->json(['message' => 'No content'], 204);
        }
    }

    public function grafik(Request $request,$token){
        $year = ['2017','2018','2019','2020','2021','2022'];

        $user = [];
        foreach ($year as $key => $value) {
            $user[] = Transaksi::where(\DB::raw("DATE_FORMAT(created_at, '%Y')"),$value)->count();
        }

    	return view('grafik')->with('year',json_encode($year,JSON_NUMERIC_CHECK))->with('nominal',json_encode($user,JSON_NUMERIC_CHECK));
    }
}
