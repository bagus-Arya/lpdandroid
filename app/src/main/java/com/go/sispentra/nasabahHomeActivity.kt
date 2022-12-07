package com.go.sispentra

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button

class nasabahHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nasabah_dashboard)
        transparentNavigation()
        supportActionBar?.hide()

        val btn_show_nasabah_profile = findViewById<Button>(R.id.btn_dt_profile_nasabah)
        val btn_show_req_penarikan = findViewById<Button>(R.id.btn_req_penarikan)
        val btn_show_tabungan = findViewById<Button>(R.id.btn_tabungan_nasabah)
        val btn_show_logout = findViewById<Button>(R.id.btn_logout_nasabah)

        btn_show_nasabah_profile.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahProfileActivity::class.java)
            startActivity(intent)
        }
        btn_show_req_penarikan.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahRequestPenarikan::class.java)
            startActivity(intent)
        }
        btn_show_tabungan.setOnClickListener{
            val intent = Intent(this@nasabahHomeActivity, nasabahTabunganActivity::class.java)
            startActivity(intent)
        }
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