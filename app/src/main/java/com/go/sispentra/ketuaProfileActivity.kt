package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import org.json.JSONException

class ketuaProfileActivity : AppCompatActivity() {
    private var loginData= LoginData(null,null,-1)
    private var getProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"
    private var ubahProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}/update"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ketua_profile)
        getAndUpdateTokenLoginData()
        basicStarter()
        layoutComponentAndListener()
        reqGetProfile(loginData,getProfileURL)
    }

    fun layoutComponentAndListener(){
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

        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            autoCompleteTxtjenisRole!!.showSoftInputOnFocus = false
            autoCompleteTxtJenisKelamin!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            autoCompleteTxtjenisRole!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            autoCompleteTxtjenisRole!!.setTextIsSelectable(true)

            autoCompleteTxtJenisKelamin!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            autoCompleteTxtJenisKelamin!!.setTextIsSelectable(true)
        } else {
            autoCompleteTxtjenisRole!!.setRawInputType(InputType.TYPE_NULL)
            autoCompleteTxtjenisRole!!.isFocusable = true

            autoCompleteTxtJenisKelamin!!.setRawInputType(InputType.TYPE_NULL)
            autoCompleteTxtJenisKelamin!!.isFocusable = true
        }

        //Button
        val btnUbah = findViewById<Button>(R.id.btn_ubah_staff)
        val btnKembali = findViewById<Button>(R.id.btn_kembali)

        //other
        val staff_textfield_editor_nama=findViewById<TextInputEditText>(R.id.staff_textfield_editor_nama)
        val staff_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.staff_textfield_editor_telepon)
        val staff_textfield_editor_password=findViewById<TextInputEditText>(R.id.staff_textfield_editor_password)


        //Listener
        autoCompleteTxtJenisKelamin.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            val view = this.currentFocus
            if (view != null) {

                // now assign the system
                // service to InputMethodManager
                val manager = getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                manager
                    .hideSoftInputFromWindow(
                        view.windowToken, 0
                    )
            }
        })
        autoCompleteTxtjenisRole.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            val view = this.currentFocus
            if (view != null) {

                // now assign the system
                // service to InputMethodManager
                val manager = getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                manager
                    .hideSoftInputFromWindow(
                        view.windowToken, 0
                    )
            }
        })
        autoCompleteTxtJenisKelamin.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
//            Toast.makeText(applicationContext, "Item: $position", Toast.LENGTH_SHORT).show()
        })
        autoCompleteTxtjenisRole.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
//            Toast.makeText(applicationContext, "Item: $position", Toast.LENGTH_SHORT).show()
        })
        btnUbah.setOnClickListener{
//            Toast.makeText(applicationContext, "Ubah Button Pressed", Toast.LENGTH_SHORT).show()
//            var text1=staff_textfield_editor_nama.text.toString()
//            var text2=staff_textfield_editor_telepon.text.toString()
//            var text3=staff_textfield_editor_password.text.toString()
//            var text4=autoCompleteTxtJenisKelamin.text.toString()
//            var text5=autoCompleteTxtjenisRole.text.toString()
//            Log.d("ubah btn", "$text1,$text2,$text3,$text4,$text5")
        }
        btnKembali.setOnClickListener{
            onBackPressed()
        }
    }

    fun updateViewProfile(dataStaff:Staff){
        val autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_jeniskelamin)
        val autoCompleteTxtjenisRole = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_role)
        val staff_textfield_editor_nama=findViewById<TextInputEditText>(R.id.staff_textfield_editor_nama)
        val staff_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.staff_textfield_editor_telepon)
        val staff_textfield_editor_password=findViewById<TextInputEditText>(R.id.staff_textfield_editor_password)
        autoCompleteTxtJenisKelamin.setText(dataStaff.jenis_kelamin)
        autoCompleteTxtjenisRole.setText(dataStaff.role)
        staff_textfield_editor_nama.setText(dataStaff.fullname)
        staff_textfield_editor_telepon.setText(dataStaff.no_telepon)
        staff_textfield_editor_password.setText(password)
    }

    fun reqGetProfile(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    Log.d("Res", response.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError) {
                    Toast.makeText(this@ketuaProfileActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is AuthFailureError) {
                    Log.d("httpfail2", error.toString())
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@ketuaProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@ketuaProfileActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                } else if (error is ServerError) {
                    Toast.makeText(this@ketuaProfileActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is NetworkError) {
                    Toast.makeText(this@ketuaProfileActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail14", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@ketuaProfileActivity, "Parse Error", Toast.LENGTH_LONG).show()
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

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"
        ubahProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}/update"
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Profile Saya")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



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