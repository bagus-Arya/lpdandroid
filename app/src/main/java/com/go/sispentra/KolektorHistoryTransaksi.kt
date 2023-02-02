package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.adapter.HistoryTransaksiAdapter
import com.rw.keyboardlistener.com.go.sispentra.adapter.TabunganTransaksiAdapter
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Transaksi
import org.json.JSONArray
import org.json.JSONException

class KolektorHistoryTransaksi : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var getHistoryTransaksiUrl = "${baseUrl.url}/api/transaksi/${loginData.token}"
    private lateinit var transaksis:ArrayList<Transaksi>
    private lateinit var mHistoryTransaksiAdapter: HistoryTransaksiAdapter
    private lateinit var refreshLayout:SwipeRefreshLayout
    private var selectedSpinner:String="Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kolektor_history_transaksi)
        basicStarter()
        getAndUpdateTokenLoginData()
        reqGetHistoryTransaksi(loginData, getHistoryTransaksiUrl)

        val spinner=findViewById<Spinner>(R.id.spinner)
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this,
            R.array.filterHistoryTransaksi, R.layout.spinner_items
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        spinner.setAdapter(adapter)

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedSpinner=spinner.selectedItem.toString()
                reqGetHistoryTransaksi(loginData, getHistoryTransaksiUrl)
//                Toast.makeText(this@bendaharaHistoryTransaksiActivity, selectedSpinner, Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        })
    }

    fun createArrayTransaksis(jsonArray: JSONArray):ArrayList<Transaksi>{
        transaksis = ArrayList<Transaksi>()
        (0 until jsonArray.length()).forEach {
            val transaksi = jsonArray.getJSONObject(it)
            transaksis.add(
                Transaksi(
                transaksi.getInt("id"),
                transaksi.getString("type_transaksi"),
                transaksi.getDouble("nominal"),
                transaksi.getString("status"),
                    transaksi.getString("tgl_transaksi"),
                    transaksi.getString("tgl_validasi_bendahara"),
                    transaksi.getString("tgl_validasi_kolektor"),
                    transaksi.getString("tgl_validasi_nasabah"),
                transaksi.getJSONObject("bukutabungan").getString("no_tabungan"),
                transaksi.getJSONObject("bukutabungan").getJSONObject("nasabah").getString("fullname"),
                transaksi.getJSONObject("bukutabungan").getJSONObject("nasabah").getJSONObject("kolektor").getString("fullname")
            )
            )
//            println("${book.get("book_name")} by ${book.get("author")}")

        }
        return transaksis
    }

    fun updateRv(transaksis:ArrayList<Transaksi>){
        mHistoryTransaksiAdapter= HistoryTransaksiAdapter(transaksis)

        val rv_history_transaksi=findViewById<RecyclerView>(R.id.rv_history_transaksi)
        rv_history_transaksi.layoutManager= LinearLayoutManager(this)
        rv_history_transaksi.adapter=mHistoryTransaksiAdapter

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

    fun reqGetHistoryTransaksi(loginData: LoginData, URL:String){
        var URL=URL+"?type_transaksi="+selectedSpinner
        val queue = Volley.newRequestQueue(this)
        val jsonObjectArray: JsonArrayRequest = object : JsonArrayRequest(
            Method.GET, URL,null,
            Response.Listener { response ->
                if(!response.isNull(0)){
                    try {
                        transaksis =createArrayTransaksis(response)
                        updateRv(transaksis)
                        Log.d("Res", transaksis.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else{
                    Toast.makeText(this@KolektorHistoryTransaksi, "Data Kosong", Toast.LENGTH_LONG).show()
                }
                refreshLayout.setRefreshing(false)
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@KolektorHistoryTransaksi, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@KolektorHistoryTransaksi, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@KolektorHistoryTransaksi, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@KolektorHistoryTransaksi, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@KolektorHistoryTransaksi, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@KolektorHistoryTransaksi, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail15", error.toString())
                }
                val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
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
        getHistoryTransaksiUrl = "${baseUrl.url}/api/transaksi/${loginData.token}"
    }

    fun basicStarter(){
        refreshLayout=findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        refreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            reqGetHistoryTransaksi(loginData, getHistoryTransaksiUrl)
        })
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("History Transaksi")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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