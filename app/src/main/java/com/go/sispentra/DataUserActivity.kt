package com.go.sispentra

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DataUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_user)

        val registpage_user = findViewById<Button>(R.id.btn_to_register_user)

        registpage_user.setOnClickListener{
            val intent = Intent(this@DataUserActivity, RegisterUserActivity::class.java)
            startActivity(intent)
        }
    }
}