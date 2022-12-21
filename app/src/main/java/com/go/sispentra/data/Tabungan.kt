package com.rw.keyboardlistener.com.go.sispentra.data

data class Tabungan(
    var id:Int,
    var no_tabungan:String,
    var nasabah_id:Int,
    var nasabah_name:String?=null,
    var nasabah_kolektor:String?=null,
    var saldo:Int?=null,
    var no_telepon:String?=null
)
