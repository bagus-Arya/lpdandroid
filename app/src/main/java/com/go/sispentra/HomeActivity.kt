package com.go.sispentra

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ketua_dashboard)

        val shownama = findViewById<TextView>(R.id.show_nama_ketua);

        val disprole: String? = intent.getStringExtra("keyStringdisplayRole").toString();
        val unama: String? = intent.getStringExtra("keyStringnama").toString();

        shownama.setText(disprole+"."+unama);

        val abtn_dt_peran = findViewById<Button>(R.id.btn_dt_peran)
        val abtn_dt_user = findViewById<Button>(R.id.btn_dt_user)

        abtn_dt_peran.setOnClickListener{
            val intent = Intent(this@HomeActivity, DataPeranActivity::class.java)
            startActivity(intent)
        }

        abtn_dt_user.setOnClickListener{
            val intent = Intent(this@HomeActivity, DataUserActivity::class.java)
            startActivity(intent)
        }
    }
}