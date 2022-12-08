package com.go.sispentra

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KolektorHomeActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_dashboard)
        transparentNavigation()
        supportActionBar?.hide()


        val btn_show_kolektor = findViewById<Button>(R.id.btn_dt_kolektor)
        val btn_nasabah = findViewById<Button>(R.id.btn_dt_kolektor_nasabah)
        val btn_validasi_penarikan = findViewById<Button>(R.id.btn_kolektor_validasi_penarikan)
        val btn_validasi_setoran = findViewById<Button>(R.id.btn_kolektor_validasi_setoran)
        val btn_history = findViewById<Button>(R.id.btn_kolektor_history)
//        val btn_penarikan = findViewById<Button>(R.id.btn_penarikan)
//        val btn_history_transaksi = findViewById<Button>(R.id.btn_history_transaksi)

        btn_show_kolektor.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorProfileActivity::class.java)
//            with(intent)
//            {
//                putExtra("keyStringuserId", id_user)
//            }
            startActivity(intent)
        }
//
        btn_nasabah.setOnClickListener{
            Log.d("Logbutton", "login button pressed")
            val intent = Intent(this@KolektorHomeActivity, KolektorNasabahActivity::class.java)
            startActivity(intent)
        }
//
        btn_validasi_penarikan.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorValidasiPenarikan::class.java)
            startActivity(intent)
        }

        btn_validasi_setoran.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorValidasiSetoran::class.java)
            startActivity(intent)
        }

        btn_history.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorHistoryTransaksi::class.java)
            startActivity(intent)
        }
//
//        btn_history_transaksi.setOnClickListener{
//            val intent = Intent(this@KolektorHomeActivity, KolektorHistoryTransaksi::class.java)
//            startActivity(intent)
//        }

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