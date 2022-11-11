package com.go.sispentra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.go.sispentra.controller.UserController
import com.go.sispentra.model.LoginModelResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAction()

    }

    fun initAction(){
        val btnlogin = findViewById<Button>(R.id.btn_login)
        btnlogin.setOnClickListener{
            login()
        }
    }

    fun login(){
        val usrnames = findViewById<EditText>(R.id.usrname)
        val pswword = findViewById<EditText>(R.id.pwsword)
        val api by lazy {UserController().endPoint}

        api.login(usrnames.text.toString(), pswword.text.toString()).enqueue(object: Callback<LoginModelResponse>{
            override fun onResponse(call: Call<LoginModelResponse>, response: Response<LoginModelResponse>) {
                if  (response.isSuccessful){
                    val useresponse = response.body()!!.data

                    val displayRole = useresponse?.display_role.toString()
                    val nameRole = useresponse?.role.toString()
                    val unama = useresponse?.nama.toString()
                    val userId = useresponse?.user_id.toString()

                    if(nameRole == "Kolektor"){
                        val intent = Intent(this@MainActivity, KolektorHomeActivity::class.java)
                        with(intent)
                        {
                            putExtra("keyStringdisplayRole", displayRole)
                            putExtra("keyStringuserId", userId)
                            putExtra("keyStringnama", unama)
                        }
                        startActivity(intent)
                    }else if(nameRole == "Bendahara"){
                        val intent = Intent(this@MainActivity, BendaharaHomeActivity::class.java)
                        with(intent)
                        {
                            putExtra("keyStringdisplayRole", displayRole)
                            putExtra("keyStringnama", unama)
                        }
                        startActivity(intent)
                    }else if(nameRole == "Ketua"){
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        with(intent)
                        {
                            putExtra("keyStringdisplayRole", displayRole)
                            putExtra("keyStringnama", unama)
                        }
                        startActivity(intent)
                    }else{
                        val intent = Intent(this@MainActivity, PageNotFound::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<LoginModelResponse>, t: Throwable) {
                Log.e("MainActivity", t.toString())
            }

        })

    }

}