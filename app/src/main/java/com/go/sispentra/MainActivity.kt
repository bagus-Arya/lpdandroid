package com.go.sispentra

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.go.sispentra.controller.UserController
import com.go.sispentra.model.LoginModelResponse
import com.rw.keyboardlistener.KeyboardUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        transparentNavigation()
        supportActionBar?.hide()

//        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        initAction()
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

    fun initAction(){
        val btnlogin = findViewById<Button>(R.id.btn_login)
        btnlogin.setOnClickListener{
            Log.d("Logbutton", "login button pressed")
            val intent = Intent(this@MainActivity, ketuaHomeActivity::class.java)
            startActivity(intent)
        //            login()
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
//    fun Activity.transparentStatusAndNavigation(
//        systemUiScrim: Int = Color.parseColor("#40000000") // 25% black
//    ) {
//        var systemUiVisibility = 0
//        // Use a dark scrim by default since light status is API 23+
//        var statusBarColor = systemUiScrim
//        //  Use a dark scrim by default since light nav bar is API 27+
//        var navigationBarColor = systemUiScrim
//        val winParams = window.attributes
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            statusBarColor = Color.TRANSPARENT
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//            navigationBarColor = Color.TRANSPARENT
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            systemUiVisibility = systemUiVisibility or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            window.decorView.systemUiVisibility = systemUiVisibility
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            winParams.flags = winParams.flags or
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            winParams.flags = winParams.flags and
//                    (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
//                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION).inv()
//            window.statusBarColor = statusBarColor
//            window.navigationBarColor = navigationBarColor
//        }
//
//        window.attributes = winParams
//    }
}