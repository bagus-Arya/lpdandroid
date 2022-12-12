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

class NasabahMobileController extends Controller
{
    public function index(Request $request,Token $token){
        $filters=$request->validate([
            'fullname'=>'string',
        ]);
        $nasabahs=Nasabah::latest()->where('staff_id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan')->filter($filters)->get();
        foreach ($nasabahs as $nasabah) {
            $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        }
        
        return $nasabahs;
        // return Nasabah::latest()->filter($filters)->get()->except($request->get('login_user')->id);
    }

    public function show(Request $request,Token $token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        $nasabah=Nasabah::where('id',$nasabah->id)->where('staff_id',$request->get('login_user')->id)->with('kolektor')->with('bukutabungan.transaksis')->firstOrFail();
        $nasabah['saldo']=(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Setoran')->where('status','validated-bendahara')->sum('nominal'))-(Transaksi::where('buku_tabungan_id',$nasabah->bukutabungan->id)
            ->where('type_transaksi','Penarikan')->whereNot('status','like','rejected%')->sum('nominal'));
        return $nasabah;
    }

    public function store(Request $request,Token $token){
        // validate input
        $validate = $request->validate([
            'fullname'=>'required|string',
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
                return response()->json(['message' => 'Server Error'], 500);
            }
        });
    }
    public function destroy(Request $request,Token $token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        return $nasabah->delete();
    }

    public function update(Request $request,Token $token,Nasabah $nasabah){
        if($request->get('login_user')->id!=$nasabah->staff_id){
            return response()->json(['message' => 'Forbiden'], 403);
        }
        $validate = $request->validate([
            'fullname'=>'required|string',
            'username'=>'required|unique:nasabahs,username,'.$nasabah->id.'|unique:staff',
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
        $nasabah->update($validate);
    }
}
