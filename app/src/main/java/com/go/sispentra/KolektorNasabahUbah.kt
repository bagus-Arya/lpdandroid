package com.go.sispentra

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Nasabah
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class KolektorNasabahUbah : AppCompatActivity() {
    val myFormat="dd-MM-yyyy"
    var nasabahId=-1
    var jenisKelamin = arrayOf("Laki-Laki", "Perempuan")
    private var loginData= LoginData(null,null,-1)
    private var getDataNasabahURL = "http://192.168.1.66:80/LPD_Android/public/api/nasabah/${loginData.token}/show/${nasabahId}"
    private var putDataNasabahURL = "http://192.168.1.66:80/LPD_Android/public/api/nasabah/${loginData.token}/update/${nasabahId}"
    private lateinit var nasabah: Nasabah

    private lateinit var adapterItemsJenisKelamin: ArrayAdapter<String>
    private lateinit var tgl_lahir_editor:TextInputEditText
    private lateinit var autoCompleteTxtJenisKelamin:AutoCompleteTextView
    private lateinit var nasabah_textfield_editor_nama:TextInputEditText
    private lateinit var nasabah_textfield_editor_password:TextInputEditText
    private lateinit var nasabah_textfield_editor_alamat:TextInputEditText
    private lateinit var nasabah_textfield_editor_no_ktp:TextInputEditText
    private lateinit var nasabah_textfield_editor_tgl_lahir:TextInputEditText
    private lateinit var nasabah_autotextfield_editor_jeniskelamin:AutoCompleteTextView
    private lateinit var nasabah_textfield_editor_telepon:TextInputEditText
    private lateinit var imageButton:ImageButton
    private lateinit var btn_ubah_nasabah:Button
    private lateinit var btn_kembali:Button

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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_nasabah_ubah)
        basicStarter()
        getAndUpdateTokenLoginData()
        setComponent()
        reqGetProfile(loginData, getDataNasabahURL)
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getDataNasabahURL = "http://192.168.1.66:80/LPD_Android/public/api/nasabah/${loginData.token}/show/${nasabahId}"
        putDataNasabahURL = "http://192.168.1.66:80/LPD_Android/public/api/nasabah/${loginData.token}/update/${nasabahId}"
    }

    fun errorValidationFetcher(response: NetworkResponse,fields:Array<String>):String{
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

    fun getViewTextNasabah():Nasabah{
//        return DataProfileStaff
        val fullname=nasabah_textfield_editor_nama.text.toString()
        val telepon=nasabah_textfield_editor_telepon.text.toString()
        val no_ktp=nasabah_textfield_editor_no_ktp.text.toString()
        val alamat= nasabah_textfield_editor_alamat.text.toString()
        val password=nasabah_textfield_editor_password.text.toString()
        val tgl_lahir=tgl_lahir_editor.text.toString()
        val jenis_kelamin=autoCompleteTxtJenisKelamin.text.toString()
        var ktp_photo="lol"
        try {
//            val image = (imageButton.getDrawable() as BitmapDrawable).bitmap
//            val outputStream = ByteArrayOutputStream()
//            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            val byteArray: ByteArray = outputStream.toByteArray()
//            ktp_photo = Base64.encodeToString(byteArray, Base64.DEFAULT)
            var drawable=imageButton.drawable
            var bitmap=drawable.toBitmap()
            var outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            var byteArray: ByteArray = outputStream.toByteArray()
            ktp_photo = Base64.encodeToString(byteArray, Base64.DEFAULT)
            val imageBytes = Base64.decode(ktp_photo, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageButton.setImageBitmap(decodedImage)

//            Toast.makeText(this@KolektorNasabahUbah, ktp_photo, Toast.LENGTH_LONG).show()
            Log.d("dead", "ok")
        } catch (e: Exception) {
            Log.e("dead", e.toString())
        }
        val nasabah=Nasabah(
            nasabahId,
            fullname,
            jenis_kelamin,
            null,
            tgl_lahir,
            ktp_photo,
            telepon,
            no_ktp,
            alamat,
            password
        )
        return nasabah
    }


    fun reqUpdateNasabah(loginData: LoginData, URL:String,nasabah: Nasabah){
        val queue = Volley.newRequestQueue(this)
        val jsonString = Gson().toJson(nasabah)
        val jsonObject= JSONObject(jsonString)
        Log.e("test", jsonObject.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.PUT, URL,jsonObject,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    updateComponent(nasabah)
                    Log.d("Res", response.toString())
                    Toast.makeText(this@KolektorNasabahUbah, "Data Updated", Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorNasabahUbah, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                }  else if (error is ServerError||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorNasabahUbah, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        val intent = Intent(this@KolektorNasabahUbah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==422){
                        val fields = arrayOf<String>(
                            "fullname",
                            "jenis_kelamin",
                            "tgl_lahir",
                            "ktp_photo",
                            "telepon",
                            "no_ktp",
                            "alamat",
                            "password")
                        val respone=errorValidationFetcher(error.networkResponse,fields)

                        Toast.makeText(this@KolektorNasabahUbah,respone, Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        val intent = Intent(this@KolektorNasabahUbah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorNasabahUbah, "Parse Error", Toast.LENGTH_LONG).show()
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

    fun reqGetProfile(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    nasabah= Nasabah(response.getInt("id"),response.getString("fullname"),
                        response.getString("jenis_kelamin"),response.getInt("staff_id"),response.getString("tgl_lahir"),
                        response.getString("ktp_photo"),response.getString("no_telepon"),response.getString("no_ktp"),
                        response.getString("alamat"),response.getString("password"))
                    updateComponent(nasabah)
                    Log.d("Res", nasabah.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is AuthFailureError || error is ServerError) {
                    Log.d("httpfail2", error.toString())
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorNasabahUbah, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
//                        Toast.makeText(this@KolektorNasabahTabungan, "Halo", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@KolektorNasabahUbah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@KolektorNasabahUbah, "Data Input Invalid", Toast.LENGTH_LONG).show()
                        Handler().postDelayed({
                            if (requestAllow){
                                reqGetProfile(loginData, URL)
                            }
                        }, 7000)
                    }
                    else if (error.networkResponse.statusCode==404){
                        val intent = Intent(this@KolektorNasabahUbah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Ada")
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Handler().postDelayed({
                            if (requestAllow){
                                reqGetProfile(loginData, URL)
                            }
                        }, 7000)
                    }
                    Log.d("httpfail11", error.toString())
                }
                else{
                    if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                        Toast.makeText(this@KolektorNasabahUbah, "Network Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail1", error.toString())
                    }  else if (error is ParseError) {
                        Toast.makeText(this@KolektorNasabahUbah, "Parse Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail14", error.toString())
                    }
                    Handler().postDelayed({
                        if (requestAllow){
                            reqGetProfile(loginData, URL)
                        }
                    }, 7000)
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

    fun updateComponent(dataNasabah:Nasabah){
        nasabah_textfield_editor_nama.setText(dataNasabah.fullname)
        nasabah_textfield_editor_telepon.setText(dataNasabah.no_telepon)
        nasabah_textfield_editor_no_ktp.setText(dataNasabah.no_ktp)
        nasabah_textfield_editor_alamat.setText(dataNasabah.alamat)
        nasabah_textfield_editor_password.setText(dataNasabah.password)
        tgl_lahir_editor.setText(dataNasabah.tgl_lahir)
        if (dataNasabah.jenis_kelamin=="Laki-Laki"){
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(0).toString(), false);
        }
        else{
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(1).toString(), false);
        }
        try {
            val pureBase64Encoded: String = dataNasabah.ktp_photo!!.substring(dataNasabah.ktp_photo!!.indexOf(",") + 1)
            val imageBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageButton.setImageBitmap(decodedImage)
            imageButton.scaleType=ImageView.ScaleType.FIT_XY
        } catch (e: Exception) {
            Log.e("dead", "lol")
        }

//        nasabah_foto_ktp=findViewById<ImageButton>(R.id.nasabah_foto_ktp)
//        btn_kembali=findViewById<Button>(R.id.btn_kembali)
    }

    fun setComponent(){
        nasabah_textfield_editor_nama=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_nama)
        nasabah_textfield_editor_password=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_password)
        nasabah_textfield_editor_alamat=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_alamat)
        nasabah_textfield_editor_no_ktp=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_no_ktp)
        nasabah_textfield_editor_tgl_lahir=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_lahir)
        nasabah_autotextfield_editor_jeniskelamin=findViewById<AutoCompleteTextView>(R.id.nasabah_autotextfield_editor_jeniskelamin)
        nasabah_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_telepon)
        imageButton=findViewById<ImageButton>(R.id.imageButton)
        btn_ubah_nasabah=findViewById<Button>(R.id.btn_ubah_nasabah)
        btn_kembali=findViewById<Button>(R.id.btn_kembali)

        //input calender
        tgl_lahir_editor=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_lahir)
        val currentDate= Calendar.getInstance().getTime();
        val sdf= SimpleDateFormat(myFormat, Locale.US)

        tgl_lahir_editor.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,tgl_lahir_editor)
        }

        //Input Select Gender
        autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.nasabah_autotextfield_editor_jeniskelamin)
        autoCompleteTxtJenisKelamin.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE));
        adapterItemsJenisKelamin = ArrayAdapter<String>(this, R.layout.list_gender, jenisKelamin)
        autoCompleteTxtJenisKelamin.setAdapter(adapterItemsJenisKelamin);
        autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(0).toString(), false);

        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            tgl_lahir_editor!!.showSoftInputOnFocus = false
            autoCompleteTxtJenisKelamin!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            tgl_lahir_editor!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            tgl_lahir_editor!!.setTextIsSelectable(true)

            autoCompleteTxtJenisKelamin!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            autoCompleteTxtJenisKelamin!!.setTextIsSelectable(true)
        } else {
            tgl_lahir_editor!!.setRawInputType(InputType.TYPE_NULL)
            tgl_lahir_editor!!.isFocusable = true

            autoCompleteTxtJenisKelamin!!.setRawInputType(InputType.TYPE_NULL)
            autoCompleteTxtJenisKelamin!!.isFocusable = true
        }

        //Listener
        autoCompleteTxtJenisKelamin.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
//            Toast.makeText(applicationContext, "Item: $position", Toast.LENGTH_SHORT).show()
        })
        tgl_lahir_editor.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
            if (hasFocus) {
                val data = DatePickerDialog(
                    this,
                    datePicker,
                    myCalender.get(Calendar.YEAR),
                    myCalender.get(Calendar.MONTH),
                    myCalender.get(Calendar.DAY_OF_MONTH)
                )
                data.setOnDismissListener {
                    tgl_lahir_editor.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
        })
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

        btn_ubah_nasabah.setOnClickListener {
            reqUpdateNasabah(loginData,putDataNasabahURL ,getViewTextNasabah())
        }

        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        imageButton.setOnClickListener {

        }
    }

    fun basicStarter(){
        val sharedPreference =  getSharedPreferences("selectedNasabah", Context.MODE_PRIVATE)
        nasabahId = sharedPreference.getInt("id",-1)
        if (nasabahId == -1) {
            val intent = Intent(this@KolektorNasabahUbah, KolektorNasabahActivity::class.java)
            intent.putExtra("message", "Value Gagal Didapatkan Dari Kolektor Nasabah Activity")
            startActivity(intent)
            finish()
        }

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Ubah Data Nasabah")
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

    private fun updateCalendar(myCalendar: Calendar, tgl_lahir_editor: TextInputEditText){
        val sdf= SimpleDateFormat(myFormat, Locale.US)
        tgl_lahir_editor.setText(sdf.format(myCalendar.time))
    }


}