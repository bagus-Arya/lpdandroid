package com.go.sispentra

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.adapter.StaffAdapter
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import org.json.JSONArray
import org.json.JSONException

class ketuaDataStaffActivity : AppCompatActivity() {
    var baseUrl= BaseURL()
    //    ${baseUrl.url}
    private var loginData= LoginData(null,null,-1)
    private var getDataStaffURL = "${baseUrl.url}/api/staff/${loginData.token}"
    private var deleteDataStaffURL = "${baseUrl.url}/api/staff/${loginData.token}/delete/"
    private lateinit var mStaffAdapter: StaffAdapter
    private lateinit var staffs:ArrayList<Staff>
//    lateinit var staffs:Array<Staff>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ketua_data_staff)
        basicStarter()
        getAndUpdateTokenLoginData()
        reqGetStaff(loginData,getDataStaffURL,null)

        val floatingButtonTambah=findViewById<FloatingActionButton>(R.id.floatingActionButton_Tambah_Staff)
        val search_ketua_staff=findViewById<TextInputEditText>(R.id.search_ketua_staff)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)


//        swipe listener

        listenerComponent(floatingButtonTambah,search_ketua_staff,refreshLayout)
        if (this.intent.extras != null && this.intent.extras!!.containsKey("message")) {
            Toast.makeText(this@ketuaDataStaffActivity, this.intent.extras!!.get("message").toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun deleteStaffReq(loginData: LoginData, staffId:Int,position:Int,mStaffAdapter:StaffAdapter){
        var URL =deleteDataStaffURL
        URL+= "$staffId"
        val queue = Volley.newRequestQueue(this)
        val  jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.DELETE, URL,null,
            Response.Listener { response ->
                Log.d("Req", "Request Success")
                try {
                    mStaffAdapter.staff.removeAt(position)
                    mStaffAdapter.notifyItemRemoved(position)
                    mStaffAdapter.notifyItemRangeChanged(position,mStaffAdapter.staff.size)
                    Log.d("staffs", staffs.toString())
                    Toast.makeText(this@ketuaDataStaffActivity, "Staff Deleted", Toast.LENGTH_SHORT).show()
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@ketuaDataStaffActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError|| error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@ketuaDataStaffActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@ketuaDataStaffActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@ketuaDataStaffActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@ketuaDataStaffActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
                    try {
                        mStaffAdapter.staff.removeAt(position)
                        mStaffAdapter.notifyItemRemoved(position)
                        mStaffAdapter.notifyItemRangeChanged(position,mStaffAdapter.staff.size)
                        Log.d("staffs", staffs.toString())
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@ketuaDataStaffActivity, "Parse Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail15", error.toString())
                }
                val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
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


    fun updateRv(staffs:ArrayList<Staff>){
        mStaffAdapter=StaffAdapter(staffs,object:StaffAdapter.OnAdapterListener{
            override fun onDelete(currentItem: Staff,position:Int) {

                try {
//                    staffs.removeAt(position)
//                    mStaffAdapter.notifyItemRemoved(position)
//                    mStaffAdapter.notifyItemRangeChanged(position,staffs.size)
                    deleteStaffReq(loginData, currentItem.id,position,mStaffAdapter)
                }
                catch (e:Exception){

                }
            }
        })

        val rv_data_staff=findViewById<RecyclerView>(R.id.rv_data_staff)
        rv_data_staff.layoutManager=LinearLayoutManager(this)
        rv_data_staff.adapter=mStaffAdapter

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

    fun createArrayStaffs(jsonArray:JSONArray):ArrayList<Staff>{
        staffs = ArrayList<Staff>()
        (0 until jsonArray.length()).forEach {
            val staff = jsonArray.getJSONObject(it)
            staffs.add(Staff(staff.getString("fullname"),staff.getString("role"),staff.getString("jenis_kelamin"),staff.getString("no_telepon"),null,staff.getInt("id"),null))
//            println("${book.get("book_name")} by ${book.get("author")}")

        }
        return staffs
    }

    fun reqGetStaff(loginData: LoginData, URL:String,searchParam:String?){
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
                    val rv_emptying = findViewById<RecyclerView>(R.id.rv_data_staff)
                    rv_emptying.visibility=View.VISIBLE
                    try {
                        staffs=createArrayStaffs(response)
                        updateRv(staffs)
                        Log.d("Res", staffs.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else{
                    val rv_emptying = findViewById<RecyclerView>(R.id.rv_data_staff)
                    rv_emptying.visibility=View.GONE
                    Toast.makeText(this@ketuaDataStaffActivity, "Data Kosong", Toast.LENGTH_LONG).show()
                }
                val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
                refreshLayout.setRefreshing(false)
            }, Response.ErrorListener { error ->
                if (error is TimeoutError || error is NoConnectionError || error is NetworkError) {
                    Toast.makeText(this@ketuaDataStaffActivity, "Network Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail1", error.toString())
                } else if (error is ServerError ||error is AuthFailureError) {
                    if(error.networkResponse.statusCode==401){
                        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                        var editor = sharedPreference.edit()
                        editor.putInt("user_id",-1)
                        editor.putString("role",null)
                        editor.putString("token",null)
                        editor.commit()
                        val intent = Intent(this@ketuaDataStaffActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else if (error.networkResponse.statusCode==403){
                        Toast.makeText(this@ketuaDataStaffActivity, "Forbiden", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==422){
                        Toast.makeText(this@ketuaDataStaffActivity, "Data Input Invalid", Toast.LENGTH_LONG).show()
                    }
                    else if (error.networkResponse.statusCode==404){
                        Toast.makeText(this@ketuaDataStaffActivity, "Data Not Found", Toast.LENGTH_LONG).show()
                    }
//                    Toast.makeText(this@ketuaDataStaffActivity, "Server Error", Toast.LENGTH_LONG).show()
                    Log.d("httpfail13", error.toString())
                } else if (error is ParseError) {
                    Toast.makeText(this@ketuaDataStaffActivity, "Parse Error", Toast.LENGTH_LONG).show()
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
        getDataStaffURL = "${baseUrl.url}/api/staff/${loginData.token}"
        deleteDataStaffURL = "${baseUrl.url}/api/staff/${loginData.token}/delete/"
    }

    fun listenerComponent(floatingButtonTambah:FloatingActionButton,search_ketua_staff:TextInputEditText,refreshLayout:SwipeRefreshLayout){
        floatingButtonTambah.setOnClickListener{
            val intent = Intent(this@ketuaDataStaffActivity, ketuaTambahDataStaffActivity::class.java)
            startActivity(intent)
        }
        refreshLayout.setOnRefreshListener(OnRefreshListener {  reqGetStaff(loginData,getDataStaffURL,null) })
        search_ketua_staff.addTextChangedListener(object : TextWatcher {

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
                    reqGetStaff(loginData, getDataStaffURL,s.toString())
//                    Toast.makeText(this@ketuaDataStaffActivity, count.toString(), Toast.LENGTH_SHORT).show()
                }
                else{
                    reqGetStaff(loginData, getDataStaffURL,null)
                }
            }
        })
    }

    fun basicStarter(){
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Data Staff")
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