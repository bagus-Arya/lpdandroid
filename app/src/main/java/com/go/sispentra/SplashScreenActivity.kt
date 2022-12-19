package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import org.json.JSONException

class SplashScreenActivity : AppCompatActivity() {
    var requestAllow: Boolean = true
    private var loginData=LoginData(null,null,-1)
    private var checkProfileUrl = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        basicStarter()
        Log.d("LOG1", loginData.toString())
        getAndUpdateTokenLoginData()
        Handler().postDelayed({
//            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
            checkTokenProfile(loginData,checkProfileUrl)
        }, 1000)

    }

    override fun onPause() {
        super.onPause()
        requestAllow=false
    }
    override fun onResume() {
        super.onResume()
        requestAllow=true
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData",Context.MODE_PRIVATE)
        loginData=LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        checkProfileUrl = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"
//        Log.d("LOG", loginData.toString())
    }

    fun checkTokenProfile(loginData:LoginData,URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putInt("user_id",response.getInt("id"))
                    editor.putString("role",response.getString("role"))
                    editor.commit()
                    Log.d("Res", response.toString())
                    if(sharedPreference.getString("role",null)=="Ketua"){
                        val intent = Intent(this@SplashScreenActivity, ketuaHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Bendahara"){
                        val intent = Intent(this@SplashScreenActivity, BendaharaHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Kolektor"){
                        val intent = Intent(this@SplashScreenActivity, KolektorHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Nasabah"){
                        val intent = Intent(this@SplashScreenActivity, nasabahHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError||error is NetworkError) {
                    Toast.makeText(this@SplashScreenActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                    }
                    Log.d("httpfail2", error.toString())
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (error is ServerError) {
                    if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@SplashScreenActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@SplashScreenActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404) {
                        Toast.makeText(
                            this@SplashScreenActivity,
                            "Data Not Found",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Log.d("httpfail13", error.toString())
                }  else if (error is ParseError) {
//                    Toast.makeText(this@SplashScreenActivity, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail14", error.toString())
                }
                Handler().postDelayed({
                    if (requestAllow){
                        checkTokenProfile(loginData,checkProfileUrl)
                    }
                }, 7000)
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

    fun basicStarter(){
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
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }
    }
}