<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\BendaharaGrafikWebController;
use App\Http\Controllers\LaporanWebController;
/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

// Route::get('/test',[TransaksiMobileController::class,'grafik']);


Route::group(['middleware'=>['CustomAuth','BendaharaCustomAuth']],function(){
    Route::get('/grafik_kolektor/{token}',[BendaharaGrafikWebController::class,'index']);
});

Route::group(['middleware'=>['CustomAuth','BendaharaCustomAuth'],'prefix'=>'laporan'],function(){
    Route::get('/penarikan/{token}',[LaporanWebController::class,'indexPenarikan']);
    Route::get('/penarikan/{token}/download',[LaporanWebController::class,'downloadPenarikan'])->name('downloadPenarikan');
    Route::get('/setoran/{token}',[LaporanWebController::class,'indexSetoran']);
    Route::get('/setoran/{token}/download',[LaporanWebController::class,'downloadSetoran'])->name('downloadSetoran');
});