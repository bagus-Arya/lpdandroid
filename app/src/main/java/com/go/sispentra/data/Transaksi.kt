package com.rw.keyboardlistener.com.go.sispentra.data

data class Transaksi(
    var id:Int,
    var type_transaksi:String,
    var nominal:Double,
    var status:String,
    var tgl_transaksi:String,
    var tgl_validasi_bendahara:String ? = null,
    var tgl_validasi_kolektor:String ? = null,
    var tgl_validasi_nasabah:String ? = null,
    var no_tabungan:String,
    var nasabah_name:String?,
    var kolektor_name:String?,
    var saldo:Int?=0
)
