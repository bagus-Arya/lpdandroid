package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private val loginUrl = "${baseUrl.url}/api/login"
//    private val loginUrl = "https://httpdump.app/dumps/c09f611b-8ffe-4d71-a668-e7dd0a9cd690"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        basicStarter()
        layoutComponentAndListener()
    }

    fun layoutComponentAndListener(){
        val btnlogin = findViewById<AppCompatButton>(R.id.submitButton)
        val username= findViewById<EditText>(R.id.username)
        val password= findViewById<EditText>(R.id.passwordText)

        btnlogin.setOnClickListener{
            Log.d("Logbutton", "login button pressed")
//            val intent = Intent(this@MainActivity,KolektorNasabahTabungan::class.java)
//            startActivity(intent)
            login(username,password)
        }
    }

    fun basicStarter(){

        setContentView(R.layout.activity_main)
        transparentNavigation()
        supportActionBar?.hide()

        //Check Keyboard
        KeyboardUtils.addKeyboardToggleListener(this, object :
            KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                if (isVisible){
                    if (Build.VERSION.SDK_INT >= 21) {
                        val window = window
                        window.clearFlags(
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS

                        )
                        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        //          window.statusBarColor = Color.TRANSPARENT
//                        window.navigationBarColor = Color.TRANSPARENT
                    }
                }
                else{
                    transparentNavigation()
                }
                Log.d("keyboard", "keyboard visible: $isVisible")
            }
        })

    }
    fun login(username:EditText,password:EditText){
        val loginRequest = JSONObject()
        loginRequest.put("username", username.getText().toString())
        loginRequest.put("password", password.getText().toString())
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, loginUrl,loginRequest,
            Response.Listener { response ->
                Log.d("Login", "Login Success")
                try {
                    val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putInt("user_id",response.getInt("user_id"))
                    editor.putString("role",response.getString("role"))
                    editor.putString("token",response.getString("token"))
                    editor.commit()
                    Log.d("Token", response.toString())
                    if(sharedPreference.getString("role",null)=="Ketua"){
                        val intent = Intent(this@MainActivity, ketuaHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Bendahara"){
                        val intent = Intent(this@MainActivity, BendaharaHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Kolektor"){
                        val intent = Intent(this@MainActivity, KolektorHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if(sharedPreference.getString("role",null)=="Nasabah"){
                        val intent = Intent(this@MainActivity, nasabahHomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError) {
                    Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        Toast.makeText(this@MainActivity, "Username Atau Password Salah", Toast.LENGTH_LONG)
                            .show()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@MainActivity, "Forbiden", Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.d("httpfail2", error.toString())
                } else if (error is ServerError) {
                    Toast.makeText(this@MainActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is NetworkError) {
                    Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail14", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@MainActivity, "Parse Error", Toast.LENGTH_LONG).show()
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