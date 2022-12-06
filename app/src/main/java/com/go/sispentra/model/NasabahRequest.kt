package com.rw.keyboardlistener.com.go.sispentra.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NasabahRequest {
    @SerializedName("nama")
    @Expose
    var nama: String? = null

    @SerializedName("alamat")
    @Expose
    var alamat: String? = null
}