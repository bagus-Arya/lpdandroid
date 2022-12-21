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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.adapter.StaffAdapter
import com.rw.keyboardlistener.com.go.sispentra.adapter.TabunganTransaksiAdapter
import com.rw.keyboardlistener.com.go.sispentra.adapter.ValidasiPenarikanAdapter
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import com.rw.keyboardlistener.com.go.sispentra.data.Transaksi
import org.json.JSONArray
import org.json.JSONException

class KolektorValidasiPenarikan : AppCompatActivity() {
    private var loginData= LoginData(null,null,-1)
    private var getDataPenarikanURL = "http://192.168.1.66:80/LPD_Android/public/api/penarikan/${loginData.token}"
    private lateinit var mPenarikanAdapter: ValidasiPenarikanAdapter
    private lateinit var penarikans:ArrayList<Transaksi>
    lateinit var refreshLayout:SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_validasi_penarikan)
        getAndUpdateTokenLoginData()
        basicStarter()
        reqGetPenarikan(loginData, getDataPenarikanURL)
    }

    fun reqGetPenarikan(loginData: LoginData, URL:String){
        val queue = Volley.newRequestQueue(this)
        val jsonObjectArray: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                if(!response.isNull(0)){
                    try {
                        penarikans=createArrayPenarikans(response)
                        updateRv(penarikans)
                        Log.d("Res", penarikans.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else{
                    Toast.makeText(this@KolektorValidasiPenarikan, "Data Kosong", Toast.LENGTH_LONG).show()
                }
                refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
                refreshLayout.setRefreshing(false)
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorValidasiPenarikan, "Network Error", Toast.LENGTH_LONG).show()
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
                        val intent = Intent(this@KolektorValidasiPenarikan, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else if (error is ServerError) {
                    if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@KolektorValidasiPenarikan, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@KolektorValidasiPenarikan, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@KolektorValidasiPenarikan, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorValidasiPenarikan, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail15", error.toString())
                }
                refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
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
        mPenarikanAdapter= ValidasiPenarikanAdapter(transaksis)


        val rv_validasi_penarikan=findViewById<RecyclerView>(R.id.rv_validasi_penarikan)
        rv_validasi_penarikan.layoutManager= LinearLayoutManager(this)
        rv_validasi_penarikan.adapter=mPenarikanAdapter

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

    fun createArrayPenarikans(jsonArray: JSONArray):ArrayList<Transaksi>{
        penarikans = ArrayList<Transaksi>()
        (0 until jsonArray.length()).forEach {
            val transaksi = jsonArray.getJSONObject(it)
            penarikans.add(Transaksi(
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
        return penarikans
    }

    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        getDataPenarikanURL = "http://192.168.1.66:80/LPD_Android/public/api/penarikan/${loginData.token}"
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Validasi Penarikan")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            reqGetPenarikan(loginData, getDataPenarikanURL)
        })

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