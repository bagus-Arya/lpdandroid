package com.go.sispentra

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.go.sispentra.controller.UserController
import com.go.sispentra.model.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KolektorProfileActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kolektor_profile)

        showDetail()
    }

    fun showDetail(){
        val id_user: String? = intent.getStringExtra("keyStringuserId").toString();
        val api by lazy { UserController().endPoint}

        if (id_user != null) {
            api.showUserDetail(id_user).enqueue(object:
                Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if  (response.isSuccessful){
                        val useresponse = response.body()!!.data
                        Log.e("KolektorProfileActivity", useresponse.toString())
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Log.e("KolektorProfileActivity", t.toString())
                }

            })
        }
    }
}