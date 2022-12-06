package com.go.sispentra

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KolektorHomeActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.kolektor_dashboard)

        val shownama = findViewById<TextView>(R.id.show_nama_kolektor);

        val disprole: String? = intent.getStringExtra("keyStringdisplayRole").toString();
        val unama: String? = intent.getStringExtra("keyStringnama").toString();
        val id_user: String? = intent.getStringExtra("keyStringuserId").toString();

        shownama.setText(disprole+"."+unama);

        val btn_show_kolektor = findViewById<Button>(R.id.btn_dt_kolektor)
        val btn_regist_nasabah = findViewById<Button>(R.id.btn_regist_nasabah)
        val btn_dt_nasabah = findViewById<Button>(R.id.btn_dt_nasabah)
        val btn_setoran = findViewById<Button>(R.id.btn_setoran)
        val btn_penarikan = findViewById<Button>(R.id.btn_penarikan)
        val btn_history_transaksi = findViewById<Button>(R.id.btn_history_transaksi)

        btn_show_kolektor.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorProfileActivity::class.java)
            with(intent)
            {
                putExtra("keyStringuserId", id_user)
            }
            startActivity(intent)
        }

        btn_regist_nasabah.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorRegistNasabahActivity::class.java)
            startActivity(intent)
        }

        btn_dt_nasabah.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorDataNasabahActivity::class.java)
            startActivity(intent)
        }

        btn_setoran.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorSetorTunaiActivity::class.java)
            startActivity(intent)
        }

        btn_penarikan.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorTarikTunaiActivity::class.java)
            startActivity(intent)
        }

        btn_history_transaksi.setOnClickListener{
            val intent = Intent(this@KolektorHomeActivity, KolektorHistoryTransaksi::class.java)
            startActivity(intent)
        }

    }

}