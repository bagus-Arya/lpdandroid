package com.go.sispentra.controller

import com.go.sispentra.ApiEndPoint
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserController {
    val endPoint:ApiEndPoint
    get() {
        val interceptor = HttpLoggingInterceptor()
        val gson = GsonBuilder().setLenient().create()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client= OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://kolektor.familytransbali.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiEndPoint::class.java)
    }
}