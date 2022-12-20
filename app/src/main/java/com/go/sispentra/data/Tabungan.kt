package com.rw.keyboardlistener.com.go.sispentra.data

data class Tabungan(
    var id:Int,
    var no_tabungan:String,
    var nasabah_id:Int,
    var nasabah_name:String,
    var nasabah_kolektor:String,
    var saldo:Int?,
    var no_telepon:String?
)
