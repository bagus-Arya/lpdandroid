<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\LoginMobileController;
use App\Http\Controllers\StaffMobileController;
use App\Http\Controllers\ProfileMobileController;
use App\Http\Controllers\NasabahMobileController;
use App\Http\Controllers\SetoranMobileController;
use App\Http\Controllers\PenarikanMobileController;
use App\Http\Controllers\TransaksiMobileController;
use App\Http\Controllers\TabunganMobileController;
/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::get('/', function () {
    return 'hi';
});


Route::post('/login',[LoginMobileController::class,'login']);


Route::group(['middleware'=>['CustomAuth']],function(){
    Route::group(['middleware'=>['KetuaCustomAuth'],'prefix'=>'staff'],function(){
        Route::get('/{token}',[StaffMobileController::class,'index']);
        Route::post('/{token}/create',[StaffMobileController::class,'store']);
        Route::delete('/{token}/delete/{staff}',[StaffMobileController::class,'destory']);
    });

    Route::group(['prefix'=>'profile'],function(){
        Route::get('/{token}',[ProfileMobileController::class,'index']);
        Route::put('/{token}/update',[ProfileMobileController::class,'update']);
    });

    Route::group(['prefix'=>'laporan'],function(){
        Route::get('/{token}/show_grafik',[TransaksiMobileController::class,'grafik']);
        Route::get('/{token}/show_setoran',[SetoranMobileController::class,'setoran']);
        Route::get('/{token}/show_penarikan',[PenarikanMobileController::class,'penarikan']);
    });

    Route::group(['middleware'=>['KolektorCustomAuth'],'prefix'=>'nasabah'],function(){
        Route::get('/{token}',[NasabahMobileController::class,'index']);
        Route::post('/{token}/create',[NasabahMobileController::class,'store']);
        Route::get('/{token}/show/{nasabah}',[NasabahMobileController::class,'show']);
        Route::put('/{token}/update/{nasabah}',[NasabahMobileController::class,'update']);
        Route::delete('/{token}/delete/{nasabah}',[NasabahMobileController::class,'destroy']);
    });

    Route::group(['prefix'=>'setoran'],function(){
        Route::get('/{token}',[SetoranMobileController::class,'index'])->middleware(['StaffCustomAuth']);
        Route::post('/{token}/create/{nasabah}',[SetoranMobileController::class,'store'])->middleware(['KolektorCustomAuth']);
        Route::put('/{token}/validasi_bendahara/{transaksi}',[SetoranMobileController::class,'updateValidasiBendahara'])->middleware(['BendaharaCustomAuth']);
        Route::put('/{token}/reject_bendahara/{transaksi}',[SetoranMobileController::class,'updateRejectBendahara'])->middleware(['BendaharaCustomAuth']);
    });

    Route::group(['prefix'=>'penarikan'],function(){
        Route::get('/{token}',[PenarikanMobileController::class,'index'])->middleware(['StaffCustomAuth']);
        Route::post('/{token}/create',[PenarikanMobileController::class,'store'])->middleware(['NasabahCustomAuth']);
        Route::put('/{token}/validasi_bendahara/{transaksi}',[PenarikanMobileController::class,'updateValidasiBendahara'])->middleware(['BendaharaCustomAuth']);
        Route::put('/{token}/reject_bendahara/{transaksi}',[PenarikanMobileController::class,'updateRejectBendahara'])->middleware(['BendaharaCustomAuth']);
        Route::put('/{token}/validasi_kolektor/{transaksi}',[PenarikanMobileController::class,'updateValidasiKolektor'])->middleware(['KolektorCustomAuth']);
        Route::put('/{token}/reject_kolektor/{transaksi}',[PenarikanMobileController::class,'updateRejectKolektor'])->middleware(['KolektorCustomAuth']);
    });

    Route::group(['prefix'=>'transaksi'],function(){
        Route::get('/{token}',[TransaksiMobileController::class,'index'])->middleware(['StaffCustomAuth']);
    });

    Route::group(['prefix'=>'tabungan'],function(){
        Route::get('/{token}',[TabunganMobileController::class,'index'])->middleware(['NasabahCustomAuth']);
        Route::get('/{token}/transaksis',[TabunganMobileController::class,'transaksis'])->middleware(['NasabahCustomAuth']);
    });

    Route::group(['prefix'=>'tabungan/staff'],function(){
        Route::get('/{token}/{nasabah}',[TabunganMobileController::class,'indexStaff'])->middleware(['KolektorCustomAuth']);
        Route::get('/{token}/{nasabah}/transaksis',[TabunganMobileController::class,'transaksisStaff'])->middleware(['KolektorCustomAuth']);
    });


    Route::delete('/logout/{token}',[LoginMobileController::class,'logout']);
});


