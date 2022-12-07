package com.go.sispentra

import android.app.DatePickerDialog
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
import com.google.android.material.textfield.TextInputEditText
import com.rw.keyboardlistener.KeyboardUtils
import java.text.SimpleDateFormat
import java.util.*

class nasabahRequestPenarikan : AppCompatActivity() {
    val myFormat="dd-MM-yyyy"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nasabah_request_penarikan)

        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Request Penarikan")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //input calender
        val tgl_penarikan_editor=findViewById<TextInputEditText>(R.id.nasabah_textfield_editor_tgl_penarikan)
        //        hide softkeyboard
        if (Build.VERSION.SDK_INT >= 21) {
            tgl_penarikan_editor!!.showSoftInputOnFocus = false
        } else if (Build.VERSION.SDK_INT >= 11) {
            tgl_penarikan_editor!!.setRawInputType(InputType.TYPE_CLASS_TEXT)
            tgl_penarikan_editor!!.setTextIsSelectable(true)

        } else {
            tgl_penarikan_editor!!.setRawInputType(InputType.TYPE_NULL)
            tgl_penarikan_editor!!.isFocusable = true
        }
        val currentDate=Calendar.getInstance().getTime();
        val sdf=SimpleDateFormat(myFormat,Locale.US)
        tgl_penarikan_editor.setText(sdf.format(currentDate.time))
        val myCalender= Calendar.getInstance()
        val datePicker= DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateCalendar(myCalender,tgl_penarikan_editor)
        }

        //Listener
        tgl_penarikan_editor.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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
                    tgl_penarikan_editor.clearFocus()
                }
                data.show()

            } else {
                // Hide your calender here
            }
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

    private fun updateCalendar(myCalendar: Calendar, tgl_lahir_editor: TextInputEditText){
        val sdf= SimpleDateFormat(myFormat, Locale.US)
        tgl_lahir_editor.setText(sdf.format(myCalendar.time))
    }
}