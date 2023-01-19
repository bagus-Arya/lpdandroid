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
import com.google.gson.Gson
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class nasabahRequestPenarikan : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var postPenarikanUrl = "${baseUrl.url}/api/penarikan/${loginData.token}/create"

    val myFormat="yyyy-MM-dd"
    lateinit var nasabah_textfield_editor_tgl_penarikan:TextInputEditText
    lateinit var nasabah_textfield_editor_nominal_setoran:TextInputEditText
    lateinit var btn_kembali_penarikan:Button
    lateinit var btn_tarik_nasabah:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nasabah_request_penarikan)
        basicStarter()
        getAndUpdateTokenLoginData()
        setComponent()
    }

    fun setComponent(){
        btn_kembali_penarikan=findViewById<Button>(R.id.btn_kembali_penarikan)
        btn_tarik_nasabah=findViewById<Button>(R.id.btn_tarik_nasabah)
        nasabah_textfield_editor_nominal_setoran=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_nominal_setoran)

        //input calender
        nasabah_textfield_editor_tgl_penarikan=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_penarikan)
        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            nasabah_textfield_editor_tgl_penarikan!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            nasabah_textfield_editor_tgl_penarikan!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            nasabah_textfield_editor_tgl_penarikan!!.setTextIsSelectable(true)

        } else {
            nasabah_textfield_editor_tgl_penarikan!!.setRawInputType(InputType.TYPE_NULL)
            nasabah_textfield_editor_tgl_penarikan!!.isFocusable = true
        }
        val currentDate=Calendar.getInstance().getTime();
        val sdf=SimpleDateFormat(myFormat,Locale.US)
        nasabah_textfield_editor_tgl_penarikan.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,nasabah_textfield_editor_tgl_penarikan)
        }

        //Listener
        nasabah_textfield_editor_tgl_penarikan.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
                    nasabah_textfield_editor_tgl_penarikan.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
        })

        btn_kembali_penarikan.setOnClickListener {
            onBackPressed()
        }
        btn_tarik_nasabah.setOnClickListener {
            var nominal: Int? =null
            try {
                nominal=nasabah_textfield_editor_nominal_setoran.text.toString().toInt()
            }
            catch (exception: Exception){
                nominal=null
            }
            reqPostPenarikanNasabah(loginData, postPenarikanUrl,nasabah_textfield_editor_tgl_penarikan.text.toString(),nominal)
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
                val intent = Intent(this@nasabahRequestPenarikan, nasabahTabunganActivity::class.java)
                intent.putExtra("message","Request Berhasil Dikirim")
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
                    Toast.makeText(this@nasabahRequestPenarikan, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@nasabahRequestPenarikan, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==400){
                        Toast.makeText(this@nasabahRequestPenarikan, "Sudah Melakukan Penarikan", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@nasabahRequestPenarikan, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        val fields = arrayOf<String>("tgl_transaksi","nominal")
                        val respone=errorValidationFetcher(error.networkResponse,fields)

                        Toast.makeText(this@nasabahRequestPenarikan,respone, Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@nasabahRequestPenarikan, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==409){
                        Toast.makeText(this@nasabahRequestPenarikan, "Saldo Tidak Mencukupi", Toast.LENGTH_LONG).show()
                    }
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@nasabahRequestPenarikan, "Parse Error", Toast.LENGTH_LONG).show()
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

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        postPenarikanUrl = "${baseUrl.url}/api/penarikan/${loginData.token}/create"
    }

    fun basicStarter(){

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Request Penarikan")
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