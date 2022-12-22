package com.go.sispentra

import android.app.DatePickerDialog
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
import android.widget.Button
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class KolektorNasabahSetoran : AppCompatActivity() {
    val myFormat="yyyy-MM-dd"
    private var loginData= LoginData(null,null,-1)
    private var nasabahId: Int=-1
    private var postSetoran = "http://192.168.1.66:80/LPD_Android/public/api/setoran/${loginData.token}/create/${nasabahId}"

    private lateinit var nasabah_textfield_editor_nominal_setoran:TextInputEditText
    private lateinit var nasabah_textfield_editor_tgl_setoran:TextInputEditText
    private lateinit var btn_setor_nasabah:Button
    private lateinit var btn_kembali_setoran:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_nasabah_setoran)
        basicStarter()
        getAndUpdateTokenLoginData()
        setComponnet()
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        postSetoran = "http://192.168.1.66:80/LPD_Android/public/api/setoran/${loginData.token}/create/${nasabahId}"
    }

    fun setComponnet(){
        //input calender
        nasabah_textfield_editor_tgl_setoran=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_setoran)
        nasabah_textfield_editor_nominal_setoran=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_nominal_setoran)
        btn_setor_nasabah=findViewById<Button>(R.id.btn_setor_nasabah)
        btn_kembali_setoran=findViewById<Button>(R.id.btn_kembali_setoran)

        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            nasabah_textfield_editor_tgl_setoran!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            nasabah_textfield_editor_tgl_setoran!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            nasabah_textfield_editor_tgl_setoran!!.setTextIsSelectable(true)

        } else {
            nasabah_textfield_editor_tgl_setoran!!.setRawInputType(InputType.TYPE_NULL)
            nasabah_textfield_editor_tgl_setoran!!.isFocusable = true
        }
        val currentDate= Calendar.getInstance().getTime();
        val sdf= SimpleDateFormat(myFormat, Locale.US)
        nasabah_textfield_editor_tgl_setoran.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,nasabah_textfield_editor_tgl_setoran)
        }

        //Listener
        nasabah_textfield_editor_tgl_setoran.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
                    nasabah_textfield_editor_tgl_setoran.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
        })
        btn_setor_nasabah.setOnClickListener {
            var nominal: Int? =null
            try {
                nominal=nasabah_textfield_editor_nominal_setoran.text.toString().toInt()
            }
            catch (exception: Exception){
                nominal=null
            }
            reqPostPenarikanNasabah(loginData,postSetoran ,nasabah_textfield_editor_tgl_setoran.text.toString(),nominal)
        }
        btn_kembali_setoran.setOnClickListener {
            onBackPressed()
        }
    }

    fun reqPostPenarikanNasabah(loginData: LoginData, URL:String,tgl_transaksi:String,nominal:Int?){
        val queue = Volley.newRequestQueue(this)
        val rootObject= JSONObject()
        rootObject.put("tgl_transaksi",tgl_transaksi)
        rootObject.put("nominal",nominal)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URL,rootObject,
            Response.Listener { response ->
//                Toast.makeText(this@ketuaTambahDataStaffActivity, "User di tambah", Toast.LENGTH_LONG).show()
                val intent = Intent(this@KolektorNasabahSetoran, KolektorNasabahTabungan::class.java)
                intent.putExtra("message","Setoran Berhasil Dibuat")
                startActivity(intent)
                finish()
                Log.d("Req", "Request Success")
                try {
                    Log.d("Res", response.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorNasabahSetoran, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError ||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorNasabahSetoran, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        val intent = Intent(this@KolektorNasabahSetoran, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==422){
                        val fields = arrayOf<String>("tgl_transaksi","nominal")
                        val respone=errorValidationFetcher(error.networkResponse,fields)
                        Toast.makeText(this@KolektorNasabahSetoran,respone, Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        val intent = Intent(this@KolektorNasabahSetoran, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Ada")
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==409){
                        Toast.makeText(this@KolektorNasabahSetoran, "Saldo Tidak Mencukupi", Toast.LENGTH_LONG).show()
                    }
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorNasabahSetoran, "Parse Error", Toast.LENGTH_LONG).show()
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

    fun basicStarter(){
        val sharedPreference =  getSharedPreferences("selectedNasabah",Context.MODE_PRIVATE)
        nasabahId = sharedPreference.getInt("id",-1)
        if (nasabahId == -1) {
            val intent = Intent(this@KolektorNasabahSetoran, KolektorNasabahActivity::class.java)
            intent.putExtra("message", "Value Gagal Didapatkan Dari Kolektor Nasabah Activity")
            startActivity(intent)
            finish()
        }

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Setoran Nasabah")
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