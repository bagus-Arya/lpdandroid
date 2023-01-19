package com.go.sispentra


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.KeyboardUtils.SoftKeyboardToggleListener
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import org.json.JSONException
import org.json.JSONObject


class DataBendaharaActivity: AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var getProfileURL = "${baseUrl.url}/api/profile/${loginData.token}"
    private var ubahProfileURL = "${baseUrl.url}/api/profile/${loginData.token}/update"
    var requestAllow: Boolean = true

    override fun onPause() {
        super.onPause()
        requestAllow=false
    }
    override fun onResume() {
        super.onResume()
        requestAllow=true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Starter Pack
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bendahara_profile)
        basicStarter()
        getAndUpdateTokenLoginData()
        layoutComponentAndListener()
        reqGetProfile(loginData,getProfileURL)
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Profile Saya")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            reqUpdateProfile(loginData,ubahProfileURL,getViewTextProfile())
        }
        btnKembali.setOnClickListener{
            onBackPressed()
        }
    }

    fun reqUpdateProfile(loginData: LoginData, URL:String,DataProfileStaff:Staff){
        val queue = Volley.newRequestQueue(this)
        val jsonString = Gson().toJson(DataProfileStaff)
        val jsonObject= JSONObject(jsonString)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.PUT, URL,jsonObject,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    updateViewProfile(DataProfileStaff)
                    Log.d("Res", response.toString())
                    Toast.makeText(this@DataBendaharaActivity, "Data Updated", Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@DataBendaharaActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                }  else if (error is ServerError ||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@DataBendaharaActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@DataBendaharaActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        val fields = arrayOf<String>("fullname","username","no_telepon","password","role","jenis_kelamin")
                        val respone=errorValidationFetcher(error.networkResponse,fields)

                        Toast.makeText(this@DataBendaharaActivity,respone, Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@DataBendaharaActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@DataBendaharaActivity, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail14", error.toString())
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
    fun errorValidationFetcher(response: NetworkResponse, fields:Array<String>):String{
        val InResponse =(JSONObject(String(response.data, Charsets.UTF_8)).getJSONObject("errors"))
        var compliteErrorMessage=""
        var i=0
        for(field in fields){
            if(InResponse.has(field)){
                compliteErrorMessage+=field+":\n["
                val fieldErrors=InResponse.getJSONArray(field)
                (0 until fieldErrors.length()).forEach {
                    compliteErrorMessage+=fieldErrors[it].toString()
                    if(it==fieldErrors.length()-1 && i==fields.size-1){
                        compliteErrorMessage+="]"
                    }
                    else if(it==fieldErrors.length()-1 && i!=fields.size-1){
                        compliteErrorMessage+="]\n\n"
                    }
                    else{
                        compliteErrorMessage+="\n"
                    }
                }
            }
            i++
        }
        return compliteErrorMessage.removeRange(compliteErrorMessage.length-2,compliteErrorMessage.length-1)

    }


    fun reqGetProfile(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    var DataProfileStaff=Staff(response.getString("fullname"),response.getString("role"),response.getString("jenis_kelamin"),response.getString("no_telepon"),response.getString("password"),-1,null)
                    updateViewProfile(DataProfileStaff)
                    Log.d("Res", response.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is AuthFailureError) {
                    Log.d("httpfail2", error.toString())
                    if (error.networkResponse.statusCode == 401) {
                        val sharedPreference =
                            getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id", -1)
                        editor.putString("role", null)
                        editor.putString("token", null)
                        editor.commit()
                        val intent = Intent(this@DataBendaharaActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                        Toast.makeText(this@DataBendaharaActivity, "Network Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail1", error.toString())
                    }  else if (error is ServerError) {
                        Toast.makeText(this@DataBendaharaActivity, "Server Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail13", error.toString())
                    }  else if (error is ParseError) {
                        Toast.makeText(this@DataBendaharaActivity, "Parse Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail14", error.toString())
                    }
                    Handler().postDelayed({
                        if (requestAllow){
                            reqGetProfile(loginData, URL)
                        }
                    }, 8000)
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

    fun updateViewProfile(dataStaff: Staff){
        val autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_jeniskelamin)
        val autoCompleteTxtjenisRole = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_role)
        val staff_textfield_editor_nama=findViewById<TextInputEditText>(R.id.staff_textfield_editor_nama)
        val staff_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.staff_textfield_editor_telepon)
        val staff_textfield_editor_password=findViewById<TextInputEditText>(R.id.staff_textfield_editor_password)
        if(dataStaff.jenis_kelamin=="Laki-Laki"){
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(0).toString(), false);
        }else{
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(1).toString(), false);
        }

        if(dataStaff.role=="Kolektor"){
            autoCompleteTxtjenisRole.setText(autoCompleteTxtjenisRole.getAdapter().getItem(0).toString(), false);
        }else if(dataStaff.role=="Bendahara")
        {
            autoCompleteTxtjenisRole.setText(autoCompleteTxtjenisRole.getAdapter().getItem(1).toString(), false);
        }
        else{
            autoCompleteTxtjenisRole.setText(autoCompleteTxtjenisRole.getAdapter().getItem(2).toString(), false);
        }
//        autoCompleteTxtJenisKelamin.setText(dataStaff.jenis_kelamin)
//        autoCompleteTxtjenisRole.setText(dataStaff.role)
        staff_textfield_editor_nama.setText(dataStaff.fullname)
        staff_textfield_editor_telepon.setText(dataStaff.no_telepon)
        staff_textfield_editor_password.setText(dataStaff.password)
    }

    fun getViewTextProfile(): Staff {
        val autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_jeniskelamin)
        val autoCompleteTxtjenisRole = findViewById<AutoCompleteTextView>(R.id.staff_autotextfield_editor_role)
        val staff_textfield_editor_nama=findViewById<TextInputEditText>(R.id.staff_textfield_editor_nama)
        val staff_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.staff_textfield_editor_telepon)
        val staff_textfield_editor_password=findViewById<TextInputEditText>(R.id.staff_textfield_editor_password)
        var DataProfileStaff= Staff(staff_textfield_editor_nama.text.toString(),autoCompleteTxtjenisRole.text.toString(),autoCompleteTxtJenisKelamin.text.toString(),staff_textfield_editor_telepon.text.toString(),staff_textfield_editor_password.text.toString(),-1,null)
        return DataProfileStaff
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getProfileURL = "${baseUrl.url}/api/profile/${loginData.token}"
        ubahProfileURL = "${baseUrl.url}/api/profile/${loginData.token}/update"
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