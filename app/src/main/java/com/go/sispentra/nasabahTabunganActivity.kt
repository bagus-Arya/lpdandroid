package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.adapter.StaffAdapter
import com.rw.keyboardlistener.com.go.sispentra.adapter.TabunganTransaksiAdapter
import com.rw.keyboardlistener.com.go.sispentra.data.*
import org.json.JSONArray
import org.json.JSONException

class nasabahTabunganActivity : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var getTabunganUrl = " ${baseUrl.url}/api/tabungan/${loginData.token}"
    private var getTabunganTransaksiUrl = " ${baseUrl.url}/api/tabungan/${loginData.token}/transaksis"
//    rv_tabungan_transaksi.visibility=View.VISIBLE
    lateinit var nasabah_nama_nasabah:TextView
    lateinit var nasabah_no_tabungan_nasabah:TextView
    lateinit var nasabah_no_telepon_nasabah:TextView
    lateinit var nasabah_nama_kolektor_nasabah:TextView
    lateinit var nasabah_saldo_nasabah:TextView
    lateinit var refreshLayout:SwipeRefreshLayout
    lateinit var rv_tabungan_transaksi:RecyclerView

    private lateinit var transaksis:ArrayList<Transaksi>
    private lateinit var mTransaksiAdapter: TabunganTransaksiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nasabah_tabungan)
        getAndUpdateTokenLoginData()
        basicStarter()
        setComponent()
        reqGetTabungan(loginData,getTabunganUrl)
        reqGetTabunganTransaksi(loginData,getTabunganTransaksiUrl)
    }

    fun updateBukuTabunganComponent(dataTabungan:Tabungan){
        nasabah_nama_nasabah.setText(dataTabungan.nasabah_name)
        nasabah_no_tabungan_nasabah.setText(dataTabungan.no_tabungan)
        nasabah_no_telepon_nasabah.setText(dataTabungan.no_telepon)
        nasabah_nama_kolektor_nasabah.setText(dataTabungan.nasabah_kolektor)
        nasabah_saldo_nasabah.setText(dataTabungan.saldo.toString())
    }

    fun reqGetTabungan(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                var dataTabungan=Tabungan(
                    response.getInt("id"),
                    response.getString("no_tabungan"),
                    response.getInt("nasabah_id"),
                    response.getJSONObject("nasabah").getString("fullname"),
                    response.getJSONObject("nasabah").getJSONObject("kolektor").getString("fullname"),
                    response.getInt("saldo"),
                    response.getJSONObject("nasabah").getString("no_telepon")
                )
                updateBukuTabunganComponent(dataTabungan)
                Log.d("Req", "Request Success")
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@nasabahTabunganActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@nasabahTabunganActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@nasabahTabunganActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@nasabahTabunganActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@nasabahTabunganActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@nasabahTabunganActivity, "Parse Error", Toast.LENGTH_LONG).show()
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
    fun reqGetTabunganTransaksi(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectArray: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                if(!response.isNull(0)){
                    rv_tabungan_transaksi.visibility=View.VISIBLE
                    try {
                        transaksis =createArrayTransaksis(response)
                        updateRv(transaksis)
                        Log.d("Res", transaksis.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else{
                    rv_tabungan_transaksi.visibility=View.GONE
                    Toast.makeText(this@nasabahTabunganActivity, "Data Kosong", Toast.LENGTH_LONG).show()
                }
                refreshLayout.setRefreshing(false)
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@nasabahTabunganActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                }else if (error is ServerError||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@nasabahTabunganActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@nasabahTabunganActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@nasabahTabunganActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@nasabahTabunganActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@nasabahTabunganActivity, "Parse Error", Toast.LENGTH_LONG).show()
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

    fun updateRv(transaksis:ArrayList<Transaksi>){
        mTransaksiAdapter= TabunganTransaksiAdapter(transaksis)

        val rv_tabungan_transaksi=findViewById<RecyclerView>(R.id.rv_tabungan_transaksi)
        rv_tabungan_transaksi.layoutManager= LinearLayoutManager(this)
        rv_tabungan_transaksi.adapter=mTransaksiAdapter

//        mRecyclerView = findViewById<View>(R.id.recyclerView)
//        mRecyclerView.setHasFixedSize(true)
//        mLayoutManager = LinearLayoutManager(this)
//        mAdapter = ExampleAdapter(mExampleList)
//
//        mRecyclerView.setLayoutManager(mLayoutManager)
//        mRecyclerView.setAdapter(mAdapter)
//
//        mAdapter.setOnItemClickListener(object : OnItemClickListener() {
//            fun onItemClick(position: Int) {
//                changeItem(position, "Clicked")
//            }
//
//            fun onDeleteClick(position: Int) {
//                removeItem(position)
//            }
//        })
    }

    fun createArrayTransaksis(jsonArray: JSONArray):ArrayList<Transaksi>{
        transaksis = ArrayList<Transaksi>()
        (0 until jsonArray.length()).forEach {
            val transaksi = jsonArray.getJSONObject(it)
            transaksis.add(Transaksi(
                transaksi.getInt("id"),
                transaksi.getString("type_transaksi"),
                transaksi.getInt("nominal"),
                transaksi.getString("status"),
                transaksi.getString("tgl_transaksi"),
                transaksi.getJSONObject("bukutabungan").getString("no_tabungan"),
                transaksi.getJSONObject("bukutabungan").getJSONObject("nasabah").getString("fullname"),
                transaksi.getJSONObject("bukutabungan").getJSONObject("nasabah").getJSONObject("kolektor").getString("fullname")
            ))
//            println("${book.get("book_name")} by ${book.get("author")}")

        }
        return transaksis
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getTabunganUrl = " ${baseUrl.url}/api/tabungan/${loginData.token}"
        getTabunganTransaksiUrl = " ${baseUrl.url}/api/tabungan/${loginData.token}/transaksis"
    }

    fun setComponent(){
        nasabah_nama_nasabah=findViewById<TextView>(R.id.nasabah_nama_nasabah)
        nasabah_no_tabungan_nasabah=findViewById<TextView>(R.id.nasabah_no_tabungan_nasabah)
        nasabah_no_telepon_nasabah=findViewById<TextView>(R.id.nasabah_no_telepon_nasabah)
        nasabah_nama_kolektor_nasabah=findViewById<TextView>(R.id.nasabah_nama_kolektor_nasabah)
        nasabah_saldo_nasabah=findViewById<TextView>(R.id.nasabah_saldo_nasabah)
        refreshLayout=findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        rv_tabungan_transaksi=findViewById<RecyclerView>(R.id.rv_tabungan_transaksi)

        refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            reqGetTabungan(loginData,getTabunganUrl)
            reqGetTabunganTransaksi(loginData,getTabunganTransaksiUrl)
        })
    }

    fun basicStarter(){
        if (this.intent.extras != null && this.intent.extras!!.containsKey("message")) {
            Toast.makeText(this@nasabahTabunganActivity, this.intent.extras!!.get("message").toString(), Toast.LENGTH_SHORT).show()
        }

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Tabungan")
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