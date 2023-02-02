<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use \App\Models\Staff;
use \App\Models\Transaksi;
use \App\Models\Nasabah;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use PDF;
use Helper;

class LaporanWebController extends Controller
{
    public function indexPenarikan(Request $request,$token){
        $userLoginData=$request->get('login_user');
        $staffs=Staff::where('role','Kolektor')->latest()->get();
        return view('bspenarikan',compact('staffs','userLoginData'));
    }

    public function downloadPenarikan(Request $request,$token){
        $validate = $request->validate([
            'id_kolektor'=>'required|integer',
            'start_date'=>'required|date_format:Y-m-d',
            'end_date'=>'required|date_format:Y-m-d',
        ]);
        $transaksiData=Transaksi::whereHas('bukutabungan.nasabah.kolektor',function ($q) use($validate){
            $q->where('id',$validate['id_kolektor']);
        })->where('status','validated-nasabah')->where('type_transaksi','Penarikan')
            ->whereDate('tgl_transaksi','>=',$validate['start_date'])
            ->whereDate('tgl_transaksi','<=',$validate['end_date'])
            ->orderBy('tgl_transaksi', 'asc')
        ->with('bukutabungan.nasabah');
        $transaksiArray=$transaksiData->get();
        $transaksiJmls=$transaksiData->sum('nominal');
        $transaksiJml= Helper::rupiah($transaksiJmls);
        $userLoginData=Staff::withTrashed()->where('id', $validate['id_kolektor'])->firstOrfail();
        // $userLoginData=$request->get('login_user');
        // $pdf = PDF::loadView('bspenarikandownload',compact('transaksiArray','transaksiJml','userLoginData','validate'));
        // $pdf->setPaper('A4','potrait');
        // return $pdf->stream('bspenarikandownload.pdf');
        return view('bspenarikandownload',compact('transaksiArray','transaksiJml','userLoginData','validate'));
    }

    public function showPenarikan(Request $request,$token){
        
        $validate = $request->validate([
            'id_kolektor'=>'required|integer',
            'start_date'=>'required|date_format:Y-m-d',
            'end_date'=>'required|date_format:Y-m-d',
        ]);
        $staffData=Staff::where('id',$validate ['id_kolektor'])->firstOrFail();
        $transaksiData=Transaksi::whereHas('bukutabungan.nasabah.kolektor',function ($q) use($validate){
            $q->where('id',$validate['id_kolektor']);
        })->where('status','validated-nasabah')->where('type_transaksi','Penarikan')
            ->whereDate('tgl_transaksi','>=',$validate['start_date'])
            ->whereDate('tgl_transaksi','<=',$validate['end_date'])
            ->orderBy('tgl_transaksi', 'asc')
        ->with('bukutabungan.nasabah');
        $transaksiArray=$transaksiData->get();
        $transaksiJmls=$transaksiData->sum('nominal');
        $transaksiJml= Helper::rupiah($transaksiJmls);
        return response()->json(
            [
                'staffData' => $staffData,
                'transaksiArray'=>$transaksiArray,
                'transaksiJml'=>$transaksiJml,
            ], 200);
    }

    public function indexSetoran(Request $request,$token){
        $userLoginData=$request->get('login_user');
        $staffs=Staff::where('role','Kolektor')->latest()->get();
        return view('bssetoran',compact('staffs','userLoginData'));
    }

    public function downloadSetoran(Request $request,$token){
        $validate = $request->validate([
            'id_kolektor'=>'required|integer',
            'start_date'=>'required|date_format:Y-m-d',
            'end_date'=>'required|date_format:Y-m-d',
        ]);
        $transaksiData=Transaksi::whereHas('bukutabungan.nasabah.kolektor',function ($q) use($validate){
            $q->where('id',$validate['id_kolektor']);
        })->where('status','validated-bendahara')->where('type_transaksi','Setoran')
            ->whereDate('tgl_transaksi','>=',$validate['start_date'])
            ->whereDate('tgl_transaksi','<=',$validate['end_date'])
            ->orderBy('tgl_transaksi', 'asc')
        ->with('bukutabungan.nasabah');
        $transaksiArray=$transaksiData->get();
        $transaksiJmls=$transaksiData->sum('nominal');
        $transaksiJml= Helper::rupiah($transaksiJmls);
        $userLoginData=Staff::withTrashed()->where('id', $validate['id_kolektor'])->firstOrfail();

        // $userLoginData=$request->get('login_user');
        // $pdf = PDF::loadView('bspenarikandownload',compact('transaksiArray','transaksiJml','userLoginData','validate'));
        // $pdf->setPaper('A4','potrait');
        // return $pdf->stream('bspenarikandownload.pdf');
        return view('bssetorandownload',compact('transaksiArray','transaksiJml','userLoginData','validate'));
    }

    public function showSetoran(Request $request,$token){
        $validate = $request->validate([
            'id_kolektor'=>'required|integer',
            'start_date'=>'required|date_format:Y-m-d',
            'end_date'=>'required|date_format:Y-m-d',
        ]);
        $staffData=Staff::where('id',$validate ['id_kolektor'])->firstOrFail();
        $transaksiData=Transaksi::whereHas('bukutabungan.nasabah.kolektor',function ($q) use($validate){
            $q->where('id',$validate['id_kolektor']);
        })->where('status','validated-bendahara')->where('type_transaksi','Setoran')
            ->whereDate('tgl_transaksi','>=',$validate['start_date'])
            ->whereDate('tgl_transaksi','<=',$validate['end_date'])
            ->orderBy('tgl_transaksi', 'asc')
        ->with('bukutabungan.nasabah');
        $transaksiArray=$transaksiData->get();
        $transaksiJmls=$transaksiData->sum('nominal');
        $transaksiJml= Helper::rupiah($transaksiJmls);
        $jumlahSetoran=$transaksiData->count();
        $targetSetoran=0;

        $from_date=$validate['start_date'];
        $to_date=$validate['end_date'];

        while (strtotime($from_date) <= strtotime($to_date)) {
            $targetSetoran=$targetSetoran+Nasabah::where('staff_id',$validate['id_kolektor'])
            ->whereDate('created_at','<=',$from_date)->count();
            $from_date = date ("Y-m-d", strtotime("+1 days", strtotime($from_date)));
        }


        return response()->json(
            [
                'staffData' => $staffData,
                'transaksiArray'=>$transaksiArray,
                'transaksiJml'=>$transaksiJml,
                'jumlahSetoran'=>$jumlahSetoran,
                'targetSetoran'=>$targetSetoran
            ], 200);
    }
}
