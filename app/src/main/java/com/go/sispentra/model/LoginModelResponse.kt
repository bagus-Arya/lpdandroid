package com.go.sispentra.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginModelResponse {
    @SerializedName("data")
    @Expose
    var data: User? = null

    class  User{
        @SerializedName("user_id")
        @Expose
        var user_id: Int? = null

        @SerializedName("nama")
        @Expose
        var nama: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("display_role")
        @Expose
        var display_role: String? = null

        @SerializedName("access_token")
        @Expose
        var access_token: String? = null

        @SerializedName("msg")
        @Expose
        var msg: String? = null
    }
}