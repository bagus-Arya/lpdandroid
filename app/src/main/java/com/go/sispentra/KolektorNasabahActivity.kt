package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.adapter.NasabahListAdapter
import com.rw.keyboardlistener.com.go.sispentra.data.*
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable

class KolektorNasabahActivity : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var getDataNasabahURL = "${baseUrl.url}/api/nasabah/${loginData.token}"
    private lateinit var search_kolektor_nasabah:TextInputEditText
    private lateinit var rv_nasabah_list:RecyclerView
    private lateinit var refreshLayout:SwipeRefreshLayout
    private lateinit var mNasabahAdapter:NasabahListAdapter
    private lateinit var nasabahs:ArrayList<Nasabah>
    private lateinit var bukuTabungans:ArrayList<Tabungan>
    private lateinit var staffs:ArrayList<Staff>
    private  lateinit var floatingButtonTambah:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_nasabah)
        getAndUpdateTokenLoginData()
        basicStarter()
        setComponent()
        reqGetNasabah(loginData,getDataNasabahURL,null)
    }

    fun setComponent(){
        floatingButtonTambah=findViewById<FloatingActionButton>(R.id.floatingActionButton_Tambah_Nasabah)
        search_kolektor_nasabah=findViewById<TextInputEditText>(R.id.search_kolektor_nasabah)
        rv_nasabah_list=findViewById<RecyclerView>(R.id.rv_nasabah_list)
        refreshLayout=findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            reqGetNasabah(
                loginData,
                getDataNasabahURL,
                null
            )
        })
        floatingButtonTambah.setOnClickListener{
            val intent = Intent(this@KolektorNasabahActivity, KolektorNasabahTambah::class.java)
            startActivity(intent)
        }
        search_kolektor_nasabah.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count != 0) {
                    reqGetNasabah(loginData,getDataNasabahURL,s.toString())
//                    Toast.makeText(this@ketuaDataStaffActivity, count.toString(), Toast.LENGTH_SHORT).show()
                }
                else{
                    reqGetNasabah(loginData,getDataNasabahURL,null)
                }
            }
        })
    }

    fun fetchNewData(jsonArray:JSONArray){
        nasabahs = ArrayList<Nasabah>()
        bukuTabungans=ArrayList<Tabungan>()
        staffs=ArrayList<Staff>()
        (0 until jsonArray.length()).forEach {
            val data = jsonArray.getJSONObject(it)
            nasabahs.add(
                Nasabah(
                    data.getInt("id"),
                    data.getString("fullname"),
                    data.getString("jenis_kelamin"),
                    data.getInt("staff_id"),
                    data.getString("tgl_lahir"),
                    data.getString("ktp_photo"),
                    data.getString("no_telepon"),
                    data.getString("no_ktp"),
                    data.getString("alamat"),
                    data.getString("password")
                )
            )

            bukuTabungans.add(
                Tabungan(
                    data.getJSONObject("bukutabungan").getInt("id"),
                    data.getJSONObject("bukutabungan").getString("no_tabungan"),
                    data.getJSONObject("bukutabungan").getInt("nasabah_id"),
                    data.getString("fullname"),
                    data.getJSONObject("kolektor").getString("fullname"),
                    data.getDouble("saldo"),
                    data.getString("no_telepon")
                )
            )
            staffs.add(
                Staff(
                    data.getJSONObject("kolektor").getString("fullname"),
                    data.getJSONObject("kolektor").getString("role"),
                    data.getJSONObject("kolektor").getString("jenis_kelamin"),
                    data.getJSONObject("kolektor").getString("no_telepon"),
                    null,
                    data.getJSONObject("kolektor").getInt("id"),
                    null))

        }
    }

    fun updateRv(nasabahs:ArrayList<Nasabah>,
                 bukuTabungans:ArrayList<Tabungan>,
                 staffs:ArrayList<Staff>){



        mNasabahAdapter= NasabahListAdapter(
            nasabahs,
            bukuTabungans,
            staffs,object: NasabahListAdapter.OnAdapterListener{
                override fun onClick(
                    nasabah: Nasabah,
                    bukuTabungan: Tabungan,
                    staff: Staff,
                    position: Int
                ) {
                    val sharedPreference =  getSharedPreferences("selectedNasabah", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putInt("id",nasabah.id)
                    editor.commit()
//                    Toast.makeText(this@KolektorNasabahActivity,sharedPreference.getInt("id",-1).toString() , Toast.LENGTH_LONG).show()
                    val intent = Intent(this@KolektorNasabahActivity, KolektorNasabahTabungan::class.java)
                    startActivity(intent)
                }

            })

        rv_nasabah_list.layoutManager= LinearLayoutManager(this)
        rv_nasabah_list.adapter=mNasabahAdapter
    }

    fun reqGetNasabah(loginData: LoginData, URL:String,searchParam:String?){
        var SURL=URL
        if(searchParam!=null){
            SURL=SURL+"?fullname=${searchParam}"
        }

        val queue = Volley.newRequestQueue(this)
        val jsonObjectArray: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, SURL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                if(!response.isNull(0)){
                    rv_nasabah_list.visibility=View.VISIBLE
                    try {
                        fetchNewData(response)
//                        staffs=createArrayStaffs(response)
                        updateRv(nasabahs,bukuTabungans,staffs)
                        Log.d("Res", response.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else{
                    rv_nasabah_list.visibility=View.GONE
                    Toast.makeText(this@KolektorNasabahActivity, "Data Kosong", Toast.LENGTH_LONG).show()
                }
                refreshLayout.setRefreshing(false)
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorNasabahActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is AuthFailureError) {

                } else if (error is ServerError ||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorNasabahActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@KolektorNasabahActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@KolektorNasabahActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@KolektorNasabahActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorNasabahActivity, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail15", error.toString())
                }
                refreshLayout.setRefreshing(false)
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>? {
                val map = HashMap<String, String>()
                map["Accept"] = "application/json"
                map["Content-Type"] = "application/json"
                return map
            }
        }
        queue.add(jsonObjectArray)
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getDataNasabahURL = "${baseUrl.url}/api/nasabah/${loginData.token}"
    }

    fun basicStarter(){
        val intent = Intent(this@KolektorNasabahActivity, KolektorNasabahTabungan::class.java)
        val sharedPreference =  getSharedPreferences("seletedNasabah", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putInt("id",-1)
        editor.commit()

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Nasabah")
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
//                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                        window.navigationBarColor = Color.TRANSPARENT
//                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                    }
                }
                else{
                    transparentNavigation()
                }
                Log.d("keyboard", "keyboard visible: $isVisible")
            }
        })

        if (this.intent.extras != null && this.intent.extras!!.containsKey("message")) {
            Toast.makeText(this@KolektorNasabahActivity, this.intent.extras!!.get("message").toString(), Toast.LENGTH_LONG).show()
        }
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