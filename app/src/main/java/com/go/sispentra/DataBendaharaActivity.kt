package com.go.sispentra


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.KeyboardUtils.SoftKeyboardToggleListener


class DataBendaharaActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        //Starter Pack
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bendahara_profile)
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Profile Saya")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Input Select Gender
        var jenisKelamin = arrayOf("Laki-Laki", "Perempuan")
        val autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_jeniskelamin)
        autoCompleteTxtJenisKelamin.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE));
        var adapterItemsJenisKelamin: ArrayAdapter<String>? =
            ArrayAdapter<String>(this, R.layout.list_gender, jenisKelamin)
        autoCompleteTxtJenisKelamin.setAdapter(adapterItemsJenisKelamin);
        autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(0).toString(), false);

        //Input Select Role
        val layoutStaffRole=findViewById<TextInputLayout>(R.id.staff_textfield_layout_role)
        layoutStaffRole.setHint(null);

        var jenisRole= arrayOf("Kolektor", "Bendahara","Ketua")
        val autoCompleteTxtjenisRole = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_role)
        autoCompleteTxtjenisRole.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE));
        var adapterItemsjenisRole: ArrayAdapter<String>? =
            ArrayAdapter<String>(this, R.layout.list_role, jenisRole)
        autoCompleteTxtjenisRole.setAdapter(adapterItemsjenisRole);
        autoCompleteTxtjenisRole.setText(autoCompleteTxtjenisRole.getAdapter().getItem(0).toString(), false);

        //Button
        val btnUbah = findViewById<Button>(R.id.btn_ubah)
        val btnKembali = findViewById<Button>(R.id.btn_kembali)


        //Listener
        autoCompleteTxtJenisKelamin.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
//            Toast.makeText(applicationContext, "Item: $position", Toast.LENGTH_SHORT).show()
        })
        autoCompleteTxtjenisRole.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
//            Toast.makeText(applicationContext, "Item: $position", Toast.LENGTH_SHORT).show()
        })
        btnUbah.setOnClickListener{
            Toast.makeText(applicationContext, "Ubah Button Pressed", Toast.LENGTH_SHORT).show()
        }
        btnKembali.setOnClickListener{
            onBackPressed()
        }

        //Check Keyboard
        KeyboardUtils.addKeyboardToggleListener(this, object : SoftKeyboardToggleListener {
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