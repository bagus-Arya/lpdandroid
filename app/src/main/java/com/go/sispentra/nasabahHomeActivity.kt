package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import org.json.JSONException

class nasabahHomeActivity : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var logOutURL = "${baseUrl.url}/api/logout/${loginData.token}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nasabah_dashboard)
        basicStarter()
        layoutComponentAndListener()
    }

    fun layoutComponentAndListener(){
        val btn_nasabah_profile_saya = findViewById<Button>(R.id.btn_nasabah_profile_saya)
        val btn_nasabah_request_penarikan = findViewById<Button>(R.id.btn_nasabah_request_penarikan)
        val btn_nasabah_tabungan = findViewById<Button>(R.id.btn_nasabah_tabungan)
        val btn_nasabah_logout = findViewById<Button>(R.id.btn_nasabah_logout)
        val btn_nasabah_validasi_penarikan = findViewById<Button>(R.id.btn_nasabah_validasi_penarikan)

        btn_nasabah_profile_saya.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahProfileActivity::class.java)
            startActivity(intent)
        }
        btn_nasabah_request_penarikan.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahRequestPenarikan::class.java)
            startActivity(intent)
        }
        btn_nasabah_tabungan.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahTabunganActivity::class.java)
            startActivity(intent)
        }
        btn_nasabah_validasi_penarikan.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahValidasiPenarikan::class.java)
            startActivity(intent)
        }

        btn_nasabah_logout.setOnClickListener{
            logOutRequest(loginData,logOutURL)
        }
    }

    fun logOutRequest(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.DELETE, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putInt("user_id",-1)
                    editor.putString("role",null)
                    editor.putString("token",null)
                    editor.commit()
                    Log.d("Res", response.toString())
                    val intent = Intent(this@nasabahHomeActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError) {
                    Toast.makeText(this@nasabahHomeActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is AuthFailureError) {
                    Log.d("httpfail2", error.toString())
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@nasabahHomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@nasabahHomeActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                } else if (error is ServerError) {
                    Toast.makeText(this@nasabahHomeActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is NetworkError) {
                    Toast.makeText(this@nasabahHomeActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail14", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@nasabahHomeActivity, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail15", error.toString())
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                return map
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        logOutURL = "${baseUrl.url}/api/logout/${loginData.token}"
//        Log.d("LOG", loginData.toString())
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.hide()
    }

    fun transparentNavigation(){
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }
}