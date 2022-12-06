package com.go.sispentra

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.go.sispentra.controller.UserController
import com.rw.keyboardlistener.com.go.sispentra.model.NasabahRequest
import com.rw.keyboardlistener.com.go.sispentra.model.NasabahResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KolektorRegistNasabahActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_nasabah)

        val btn_kolektor_register_nasabah = findViewById<Button>(R.id.btn_register_nasabah)

        btn_kolektor_register_nasabah.setOnClickListener{
            postNasabah()
        }

    }
    fun postNasabah(){
        val nasabahReq = NasabahRequest()

        val namas = findViewById<EditText>(R.id.btn_rgst_nama)
        val alamats = findViewById<EditText>(R.id.btn_alamat)
        val api by lazy { UserController().endPoint}
        nasabahReq.nama = namas.text.toString()
        nasabahReq.alamat = alamats.text.toString()

            api.createNasabah(nasabahReq).enqueue(object:
            Callback<NasabahResponse> {

            override fun onResponse(call: Call<NasabahResponse>, response: Response<NasabahResponse>) {
                if  (response.isSuccessful){
                    val intent = Intent(this@KolektorRegistNasabahActivity, KolektorHomeActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<NasabahResponse>, t: Throwable) {
                Log.e("Failed", t.message.toString())
            }

        })
    }
}