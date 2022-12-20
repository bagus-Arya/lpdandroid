package com.go.sispentra

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Nasabah
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*


class nasabahProfileActivity : AppCompatActivity() {
    private var loginData= LoginData(null,null,-1)
    private var getProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"

    val myFormat="yyyy-MM-dd"
    var jenisKelamin = arrayOf("Laki-Laki", "Perempuan")

    lateinit var tgl_lahir_editor:TextInputEditText
    lateinit var autoCompleteTxtJenisKelamin:AutoCompleteTextView
    lateinit var adapterItemsJenisKelamin: ArrayAdapter<String>
    lateinit var nasabah_textfield_editor_nama:TextInputEditText
    lateinit var nasabah_textfield_editor_telepon:TextInputEditText
    lateinit var nasabah_textfield_editor_no_ktp:TextInputEditText
    lateinit var nasabah_textfield_editor_alamat:TextInputEditText
    lateinit var nasabah_foto_ktp: ImageButton
    lateinit var btn_kembali:Button
    lateinit var btn_nasabah_tabungan:Button

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
        setContentView(R.layout.nasabah_profile)
        basicStarter()
        setComponent()
        getAndUpdateTokenLoginData()
        reqGetProfile(loginData, getProfileURL)
    }

    fun setComponent(){
        nasabah_textfield_editor_nama=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_nama)
        nasabah_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_telepon)
        nasabah_textfield_editor_no_ktp=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_no_ktp)
        nasabah_textfield_editor_alamat=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_alamat)
        nasabah_foto_ktp=findViewById<ImageButton>(R.id.nasabah_foto_ktp)
        btn_kembali=findViewById<Button>(R.id.btn_kembali)
        btn_nasabah_tabungan=findViewById<Button>(R.id.btn_nasabah_tabungan)


        //input calender
        tgl_lahir_editor=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_lahir)
        val currentDate=Calendar.getInstance().getTime();
        val sdf=SimpleDateFormat(myFormat,Locale.US)
        tgl_lahir_editor.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,tgl_lahir_editor)
        }

        //Input Select Gender
        autoCompleteTxtJenisKelamin = findViewById<AutoCompleteTextView>(R.id.nasabah_autotextfield_editor_jeniskelamin)
        autoCompleteTxtJenisKelamin.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE));
        adapterItemsJenisKelamin= ArrayAdapter<String>(this, R.layout.list_gender, jenisKelamin)
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
        tgl_lahir_editor.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
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
                val data=DatePickerDialog(this,
                    datePicker,
                    myCalender.get(Calendar.YEAR),
                    myCalender.get(Calendar.MONTH),
                    myCalender.get(Calendar.DAY_OF_MONTH))
                data.setOnDismissListener {
                    tgl_lahir_editor.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
        })
        autoCompleteTxtJenisKelamin.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
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
        btn_kembali.setOnClickListener(){
            onBackPressed()
        }
        btn_nasabah_tabungan.setOnClickListener {
            val intent = Intent(this@nasabahProfileActivity, nasabahTabunganActivity::class.java)
            startActivity(intent)
        }
    }

    fun updateComponent(dataNasabah:Nasabah){
        nasabah_textfield_editor_nama.setText(dataNasabah.fullname)
        nasabah_textfield_editor_telepon.setText(dataNasabah.no_telepon)
        nasabah_textfield_editor_no_ktp.setText(dataNasabah.no_ktp)
        nasabah_textfield_editor_alamat.setText(dataNasabah.alamat)
        if (dataNasabah.jenis_kelamin=="Laki-Laki"){
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(0).toString(), false);
        }
        else{
            autoCompleteTxtJenisKelamin.setText(autoCompleteTxtJenisKelamin.getAdapter().getItem(1).toString(), false);
        }
        try {
            val pureBase64Encoded: String = dataNasabah.ktp_photo.substring(dataNasabah.ktp_photo.indexOf(",") + 1)
            val imageBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            nasabah_foto_ktp.setImageBitmap(decodedImage)
            nasabah_foto_ktp.scaleType=ImageView.ScaleType.FIT_XY
        } catch (e: Exception) {
            Log.e("dead", "lol")
        }

//        nasabah_foto_ktp=findViewById<ImageButton>(R.id.nasabah_foto_ktp)
//        btn_kembali=findViewById<Button>(R.id.btn_kembali)
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getProfileURL = "http://192.168.1.66:80/LPD_Android/public/api/profile/${loginData.token}"
    }

    fun reqGetProfile(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    var dataNasabah=Nasabah(response.getInt("id"),response.getString("fullname"),
                        response.getString("jenis_kelamin"),response.getInt("staff_id"),response.getString("tgl_lahir"),
                        response.getString("ktp_photo"),response.getString("no_telepon"),response.getString("no_ktp"),response.getString("alamat"))
                    updateComponent(dataNasabah)
                    Log.d("Res", dataNasabah.toString())
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
                        val intent = Intent(this@nasabahProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                        Toast.makeText(this@nasabahProfileActivity, "Network Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail1", error.toString())
                    }  else if (error is ServerError) {
                        Toast.makeText(this@nasabahProfileActivity, "Server Error", Toast.LENGTH_LONG).show()
                        Log.d("httpfail13", error.toString())
                    }  else if (error is ParseError) {
                        Toast.makeText(this@nasabahProfileActivity, "Parse Error", Toast.LENGTH_LONG).show()
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

    private fun updateCalendar(myCalendar:Calendar, tgl_lahir_editor: TextInputEditText){
        val sdf=SimpleDateFormat(myFormat,Locale.US)
        tgl_lahir_editor.setText(sdf.format(myCalendar.time))
    }

}