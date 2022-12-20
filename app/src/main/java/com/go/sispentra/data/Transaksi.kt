package com.rw.keyboardlistener.com.go.sispentra.data

data class Transaksi(
    var id:Int,
    var type_transaksi:String,
    var nominal:Int,
    var status:String,
    var tgl_transaksi:String,
    var no_tabungan:String,
    var nasabah_name:String?,
    var kolektor_name:String?
)
