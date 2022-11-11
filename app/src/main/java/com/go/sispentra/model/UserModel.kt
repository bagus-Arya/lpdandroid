package com.go.sispentra.model

data class UserModel (
    val data: List<Datas>
        ){
    data class Datas (
        val nama: String?,
        val jenis_kelamin: String?
        )
}