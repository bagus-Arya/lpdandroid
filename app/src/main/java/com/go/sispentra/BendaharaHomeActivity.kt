package com.go.sispentra

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BendaharaHomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bendahara_dashboard)
        transparentNavigation()
        supportActionBar?.hide()

//        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
//        val shownama = findViewById<TextView>(R.id.show_nama_bendahara);
//
//        val disprole: String? = intent.getStringExtra("keyStringdisplayRole").toString();
//        val unama: String? = intent.getStringExtra("keyStringnama").toString();

//        shownama.setText(disprole+"."+unama);


//        val btn_show_dt_bendahara = findViewById<Button>(R.id.btn_dt_bendahara)
        val btn_show_dt_grafik_kinerja = findViewById<Button>(R.id.btn_dt_grafik_kinerja)
        val btn_show_dt_validasi_setoran = findViewById<Button>(R.id.btn_dt_validasi_setoran)
        val btn_show_dt_validasi_penarikan = findViewById<Button>(R.id.btn_dt_validasi_penarikan)
        val btn_show_dt_laporan_setoran = findViewById<Button>(R.id.btn_dt_laporan_setoran)
        val btn_show_dt_laporan_penarikan = findViewById<Button>(R.id.btn_dt_laporan_penarikan)
        val btn_show_dt_profile_bendahara = findViewById<Button>(R.id.btn_dt_profile_bendahara)

//        btn_show_dt_bendahara.setOnClickListener{
//            val intent = Intent(this@BendaharaHomeActivity, DataBendaharaActivity::class.java)
//            startActivity(intent)
//        }

        btn_show_dt_grafik_kinerja.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, GrafikBendaharaActivity::class.java)
            startActivity(intent)
        }

        btn_show_dt_validasi_setoran.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, ValidasiSetoranBendaharaActivity::class.java)
            startActivity(intent)
        }

        btn_show_dt_validasi_penarikan.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, ValidasiPenarikanBendaharaActivity::class.java)
            startActivity(intent)
        }

        btn_show_dt_laporan_setoran.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, LaporanSetoranBendaharaActivity::class.java)
            startActivity(intent)
        }

        btn_show_dt_laporan_penarikan.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, LaporanPenarikanBendaharaActivity::class.java)
            startActivity(intent)
        }

        btn_show_dt_profile_bendahara.setOnClickListener{
            val intent = Intent(this@BendaharaHomeActivity, DataBendaharaActivity::class.java)
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