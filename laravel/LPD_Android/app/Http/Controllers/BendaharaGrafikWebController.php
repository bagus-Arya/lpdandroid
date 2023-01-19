<?php

namespace App\Http\Controllers;
use \App\Models\Nasabah;
use \App\Models\BukuTabungan;
use \App\Models\Transaksi;
use \App\Models\Staff;
use Illuminate\Http\Request;

class BendaharaGrafikWebController extends Controller
{
    public function index(Request $request,$token){
        return view('bsgrafik',compact('token'));
    }
    public function getData(Request $request,$token){
        // return Transaksi::with('bukutabungan.nasabah.kolektor')->get();
        try{

            $request['from_date']=\Carbon\Carbon::parse($request['from_date'])->format('Y-m-d');
            $request['to_date']=\Carbon\Carbon::parse($request['to_date'])->format('Y-m-d');

            if($request['from_date']>$request['to_date']){
                $tmp=$request['from_date'];
                $request['from_date']=$request['to_date'];
                $request['to_date']=$tmp;
            }

            $staffs=Staff::whereHas('nasabah.bukutabungan.transaksis',function($q){
                $q->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                ->orWhere('type_transaksi','Penarikan')->where('status','validated-kolektor');
            })->get();
            $colorsData=[];
            $labelsData=[];
            $pieChartData=[];
            $barChartData=[];

            // Generate label and color
            foreach ($staffs as $staff){
                array_push($colorsData,'rgb('.rand(100,255).','.rand(100,255).','.rand(100,255).')');
                array_push($labelsData,$staff->fullname);
            }

            // GeneratePie Data
            $pieChartData['labels']=[];
            $pieChartData['backgroundColor']=[];
            $pieChartData['data']=[];
            foreach ($staffs as $key=>$staff){
                $from_date=$request['from_date'];
                $to_date=$request['to_date'];
                array_push($pieChartData['labels'],$labelsData[$key]);
                array_push($pieChartData['backgroundColor'],$colorsData[$key]);
                $transaksis=Transaksi::where(function ($q){
                                $q->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                                ->orWhere('type_transaksi','Penarikan')->where('status','validated-kolektor');
                            })->whereHas('bukutabungan.nasabah.kolektor',function ($s) use($staff){
                                $s->where('id',$staff->id);
                            })->whereDate('created_at', '>=', $from_date)->whereDate('created_at', '<=', $to_date)->count();
                array_push($pieChartData['data'],$transaksis);
                // while (strtotime($from_date) <= strtotime($to_date)) {
                //     array_push($pieChartData['data'],
                //     [
                //         "x"=>$from_date,
                //         "y"=>Transaksi::where(function ($q){
                //             $q->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                //             ->orWhere('type_transaksi','Penarikan')->where('status','validated-kolektor');
                //         })->whereHas('bukutabungan.nasabah.kolektor')->whereDate('created_at',$from_date)->count(),
                //     ]
                //     );
                //     $from_date = date ("Y-m-d", strtotime("+1 days", strtotime($from_date)));
                // }

            }
            // Generate Bar Data
            $barChartData['dataArray']=[];
            foreach ($staffs as $key=>$staff){
                $from_date=$request['from_date'];
                $to_date=$request['to_date'];
                $label=$staff->fullname;
                $backgroundColor=$colorsData[$key];
                $borderColor=$backgroundColor;
                $dataInside=[];
                while (strtotime($from_date) <= strtotime($to_date)) {
                    array_push($dataInside,
                    [
                        "x"=>$from_date,
                        "y"=>Transaksi::where(function ($q){
                            $q->where('type_transaksi','Setoran')->where('status','validated-bendahara')
                            ->orWhere('type_transaksi','Penarikan')->where('status','validated-kolektor');
                        })->whereHas('bukutabungan.nasabah.kolektor',function ($s) use($staff){
                            $s->where('id',$staff->id);
                        })->whereDate('created_at',$from_date)->count(),
                    ]
                    );
                    $from_date = date ("Y-m-d", strtotime("+1 days", strtotime($from_date)));
                }
                array_push($barChartData['dataArray'],
                [
                    "label"=>$label,
                    "backgroundColor"=>$backgroundColor,
                    "borderColor"=>$borderColor,
                    "data"=> $dataInside
                ]
                );
            }

            return response()->json([
                'piechart' => $pieChartData,
                'barchart'=> $barChartData
            ], 200);
        }
        catch (\Throwable $th) {
            // return response()->json(['message' => $th], 500);
            return $th;
        }
    }
}
