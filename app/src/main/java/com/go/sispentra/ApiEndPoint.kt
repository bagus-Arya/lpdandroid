package com.go.sispentra

import com.go.sispentra.model.LoginModelResponse
import com.go.sispentra.model.RoleModel
import com.go.sispentra.model.UserModel
import com.rw.keyboardlistener.com.go.sispentra.model.NasabahRequest
import com.rw.keyboardlistener.com.go.sispentra.model.NasabahResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {

    @GET("dt/users/{id_user}")
    fun showUserDetail(@Path("id_user")id_user:String): Call<UserModel>

    @FormUrlEncoded
    @POST("dt/post/roles")
    fun createRole(
        @Field("nama") nama:String,
        @Field("display_name") display_name:String
    ): Call<RoleModel>

    @POST("dt-ns/post/3")
    fun createNasabah(
        @Body req:NasabahRequest
    ): Call<NasabahResponse>

    @FormUrlEncoded
    @POST("api/v1/auth/login")
    fun login(
        @Field("username") username:String,
        @Field("password") password:String
    ): Call<LoginModelResponse>

}