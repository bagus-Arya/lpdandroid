package com.go.sispentra

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Nasabah
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class KolektorNasabahTambah : AppCompatActivity() {
    val myFormat="yyyy-MM-dd"
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private lateinit var nasabah_textfield_editor_username:TextInputEditText
    private lateinit var nasabah_textfield_editor_nama:TextInputEditText
    private lateinit var nasabah_autotextfield_editor_jeniskelamin:AutoCompleteTextView
    private lateinit var nasabah_textfield_editor_tgl_lahir:TextInputEditText
    private lateinit var nasabah_textfield_editor_telepon:TextInputEditText
    private lateinit var nasabah_textfield_editor_no_ktp:TextInputEditText
    private lateinit var nasabah_textfield_editor_alamat:TextInputEditText
    private lateinit var nasabah_textfield_editor_password:TextInputEditText
    private lateinit var imageButton:ImageButton
    private lateinit var btn_tambah_nasabah:Button
    private lateinit var btn_kembali:Button
    private lateinit var adapterItemsJenisKelamin: ArrayAdapter<String>
    private var maxSizePicture=1*1000000
    private var qualityPictureCompressed=30
    private val GALLERY = 1
    private val CAMERA = 2

    private var loginData= LoginData(null,null,-1)
    private var postDataNasabahURL = "${baseUrl.url}/api/nasabah/${loginData.token}/create"

    var jenisKelamin = arrayOf("Laki-Laki", "Perempuan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_nasabah_tambah)
        basicStarter()
        getAndUpdateTokenLoginData()
        setCompnent()
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        postDataNasabahURL = "${baseUrl.url}/api/nasabah/${loginData.token}/create"
    }

    fun getViewTextNasabah():Nasabah{
//        return DataProfileStaff
        val fullname=nasabah_textfield_editor_nama.text.toString()
        val username=nasabah_textfield_editor_username.text.toString()
        val telepon=nasabah_textfield_editor_telepon.text.toString()
        val no_ktp=nasabah_textfield_editor_no_ktp.text.toString()
        val alamat= nasabah_textfield_editor_alamat.text.toString()
        val password=nasabah_textfield_editor_password.text.toString()
        val tgl_lahir=nasabah_textfield_editor_tgl_lahir.text.toString()
        val jenis_kelamin=nasabah_autotextfield_editor_jeniskelamin.text.toString()
        var ktp_photo=""
        try {
//            val image = (imageButton.getDrawable() as BitmapDrawable).bitmap
//            val outputStream = ByteArrayOutputStream()
//            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            val byteArray: ByteArray = outputStream.toByteArray()
//            ktp_photo = Base64.encodeToString(byteArray, Base64.DEFAULT)
            var drawable=imageButton.drawable
            var bitmap=drawable.toBitmap()
            var outputStream = ByteArrayOutputStream()
            if(bitmap.allocationByteCount>maxSizePicture){
                bitmap.compress(Bitmap.CompressFormat.JPEG, qualityPictureCompressed, outputStream)
            }
            else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            var byteArray: ByteArray = outputStream.toByteArray()
            ktp_photo = Base64.encodeToString(byteArray, Base64.DEFAULT)

            Log.d("dead", "ok")
        } catch (e: Exception) {
            Log.e("dead", e.toString())
        }
        val nasabah= Nasabah(
            -1,
            fullname,
            jenis_kelamin,
            loginData.user_id,
            tgl_lahir,
            ktp_photo,
            telepon,
            no_ktp,
            alamat,
            password,
            username
        )
        return nasabah
    }

    fun setCompnent(){
        nasabah_textfield_editor_username=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_username)
        nasabah_textfield_editor_nama=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_nama)
        nasabah_autotextfield_editor_jeniskelamin=findViewById<AutoCompleteTextView>(R.id.nasabah_autotextfield_editor_jeniskelamin)
        nasabah_textfield_editor_tgl_lahir=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_lahir)
        nasabah_textfield_editor_telepon=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_telepon)
        nasabah_textfield_editor_no_ktp=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_no_ktp)
        nasabah_textfield_editor_alamat=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_alamat)
        nasabah_textfield_editor_password=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_password)
        imageButton=findViewById<ImageButton>(R.id.imageButton)
        btn_tambah_nasabah=findViewById<Button>(R.id.btn_tambah_nasabah)
        btn_kembali=findViewById<Button>(R.id.btn_kembali)
        imageButton.scaleType=ImageView.ScaleType.FIT_XY

        //input calender
        nasabah_textfield_editor_tgl_lahir=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_lahir)
        val currentDate= Calendar.getInstance().getTime();
        val sdf= SimpleDateFormat(myFormat, Locale.US)
        nasabah_textfield_editor_tgl_lahir.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,nasabah_textfield_editor_tgl_lahir)
        }

        //Input Select Gender

        nasabah_autotextfield_editor_jeniskelamin = findViewById<AutoCompleteTextView>(R.id.nasabah_autotextfield_editor_jeniskelamin)
        nasabah_autotextfield_editor_jeniskelamin.setDropDownBackgroundDrawable(ColorDrawable(Color.WHITE));
        adapterItemsJenisKelamin=
            ArrayAdapter<String>(this, R.layout.list_gender, jenisKelamin)
        nasabah_autotextfield_editor_jeniskelamin.setAdapter(adapterItemsJenisKelamin);
        nasabah_autotextfield_editor_jeniskelamin.setText(nasabah_autotextfield_editor_jeniskelamin.getAdapter().getItem(0).toString(), false);

        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            nasabah_textfield_editor_tgl_lahir!!.showSoftInputOnFocus = false
            nasabah_autotextfield_editor_jeniskelamin!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            nasabah_textfield_editor_tgl_lahir!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            nasabah_textfield_editor_tgl_lahir!!.setTextIsSelectable(true)

            nasabah_autotextfield_editor_jeniskelamin!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            nasabah_autotextfield_editor_jeniskelamin!!.setTextIsSelectable(true)
        } else {
            nasabah_textfield_editor_tgl_lahir!!.setRawInputType(InputType.TYPE_NULL)
            nasabah_textfield_editor_tgl_lahir!!.isFocusable = true

            nasabah_autotextfield_editor_jeniskelamin!!.setRawInputType(InputType.TYPE_NULL)
            nasabah_autotextfield_editor_jeniskelamin!!.isFocusable = true
        }

        //Listener
        nasabah_autotextfield_editor_jeniskelamin.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
        })
        nasabah_textfield_editor_tgl_lahir.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
                    nasabah_textfield_editor_tgl_lahir.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
        })
        nasabah_autotextfield_editor_jeniskelamin.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_tambah_nasabah.setOnClickListener {
            reqPostNasabah(loginData,postDataNasabahURL,getViewTextNasabah())
        }
        imageButton.setOnClickListener {
            showPictureDialog()
        }
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

    fun reqPostNasabah(loginData: LoginData, URL:String,nasabah: Nasabah){
        val queue = Volley.newRequestQueue(this)
        val jsonString = Gson().toJson(nasabah)
        val jsonObject= JSONObject(jsonString)
        Log.e("test", jsonObject.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URL,jsonObject,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                val intent = Intent(this@KolektorNasabahTambah, KolektorNasabahActivity::class.java)
                intent.putExtra("message","Nasabah Berhasil Ditambahkan")
                startActivity(intent)
                finish()
                try {

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorNasabahTambah, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                }  else if (error is ServerError ||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorNasabahTambah, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        val intent = Intent(this@KolektorNasabahTambah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==422){
                        val fields = arrayOf<String>(
                            "username",
                            "fullname",
                            "jenis_kelamin",
                            "tgl_lahir",
                            "ktp_photo",
                            "telepon",
                            "no_ktp",
                            "alamat",
                            "password")
                        val respone=errorValidationFetcher(error.networkResponse,fields)

                        Toast.makeText(this@KolektorNasabahTambah,respone, Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        val intent = Intent(this@KolektorNasabahTambah, KolektorNasabahActivity::class.java)
                        intent.putExtra("message","Data Nasabah Tidak Boleh Diakses")
                        startActivity(intent)
                        finish()
                    }
                    var body = String(error.networkResponse.data)
                    Log.d("httpfail13", body)
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorNasabahTambah, "Parse Error", Toast.LENGTH_LONG).show()
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

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Pilih Media")
        val pictureDialogItems = arrayOf("Ambil Dari Gallery", "Ambil Dari Kamera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED)
        {
            return
        }
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    Log.e("before",bitmap.allocationByteCount.toString())

                    var outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, qualityPictureCompressed, outputStream)
                    var byteArray: ByteArray = outputStream.toByteArray()
                    Log.e("after",byteArray.size.toString())

                    if(byteArray.size>(1*maxSizePicture)){
                        Toast.makeText(this@KolektorNasabahTambah, "Image To Big", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this@KolektorNasabahTambah, "Image Saved!", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this@KolektorNasabahTambah,bitmap.allocationByteCount.toString() , Toast.LENGTH_LONG).show()
                        imageButton!!.setImageBitmap(bitmap)
                    }
                    //                    val bitmapCompressed = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//                    val bitmapCompressed=getResizedBitmap(bitmap,1000)
//                    Log.e("after", byteArray.size.toString())
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@KolektorNasabahTambah, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val bitmap = data!!.extras!!.get("data") as Bitmap
            Log.e("before",bitmap.allocationByteCount.toString())

            var outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, qualityPictureCompressed, outputStream)
            var byteArray: ByteArray = outputStream.toByteArray()
            Log.e("after",byteArray.size.toString())

            if(byteArray.size>(1*maxSizePicture)){
                Toast.makeText(this@KolektorNasabahTambah, "Image To Big", Toast.LENGTH_SHORT).show()
            }
            else{
                imageButton!!.setImageBitmap(bitmap)
                Toast.makeText(this@KolektorNasabahTambah, "Image Saved!", Toast.LENGTH_SHORT).show()
//            Toast.makeText(this@KolektorNasabahUbah,thumbnail.allocationByteCount.toString() , Toast.LENGTH_LONG).show()
            }


        }
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Tambah Nasabah")
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