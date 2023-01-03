package com.go.sispentra

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.rw.keyboardlistener.KeyboardUtils
import com.rw.keyboardlistener.com.go.sispentra.data.BaseURL
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import java.io.File
import java.io.FileOutputStream
import java.util.*

//



class LaporanPenarikanBendaharaActivity : AppCompatActivity() {
    val CHANNEL_ID="PDF_NOTIFICATION_CHANNEL"
    val CHANNEL_NAME="PDF_NOTIFICATION_NAME"
    val NOTIFICATION_ID=0
    var baseUrl= BaseURL()
    private var loginData= LoginData(null,null,-1)
    private var urls = "${baseUrl.url}/laporan/penarikan/${loginData.token}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bendahara_laporan_penarikan)
        basicStarter()
        createNotificationChannel()


        val webView=findViewById<WebView>(R.id.webview_laporan_penarikan)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(urls)
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            Log.d("webview", "Hello Download trigger")
            Log.d("webview", url.length.toString())
//            Log.d("webview", userAgent)
//            Log.d("webview", contentDisposition)
//            Log.d("webview", mimetype)
//            Log.d("webview", contentLength.toString())
            savePDF(url)
        }

    }
    fun savePDF(dataBase64:String){
        try {
            var filename="laporan-penarikan_"+ randomString(5)+ ".pdf"
            val dwldsPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) ,filename)
            var pureBase64= dataBase64.substring(dataBase64.indexOf(",") + 1)
            val pdfAsBytes: ByteArray = Base64.decode(pureBase64, 0)
            val os: FileOutputStream
            os = FileOutputStream(dwldsPath, false)
            os.write(pdfAsBytes)
            os.flush()
            os.close()

            var uri=Uri.parse("content"+(Uri.fromFile(dwldsPath).toString().substring(4)))
            Log.e("pdf", uri.toString())
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, "application/pdf")
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val notification = NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("PDF Downloaded")
                .setContentText("PDF name : "+filename+" Succesfully Downloaded")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()
            val notificationManager=NotificationManagerCompat.from(this)
            notificationManager.notify(NOTIFICATION_ID,notification)

        } catch (e: Exception) {
            val notification = NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("PDF Failed To Download")
                .setContentText("PDF Error")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            val notificationManager=NotificationManagerCompat.from(this)
            notificationManager.notify(NOTIFICATION_ID,notification)
            Log.e("pdf", e.toString())
        }
    }

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                    lightColor=Color.GREEN
                enableLights(true)
            }
            val manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    fun randomString(size:Int):String{
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val sb = StringBuilder()
        for (i in 0 until size) {
            val c = characters[random.nextInt(characters.length)]
            sb.append(c)
        }
        return sb.toString()
    }
    fun getAndUpdateTokenLoginData(){
        val sharedPreference =  getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        loginData= LoginData(sharedPreference.getString("token",null),sharedPreference.getString("role",null),sharedPreference.getInt("user_id",-1))
        urls = "${baseUrl.url}/laporan/penarikan/${loginData.token}"
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
    fun basicStarter(){
        getAndUpdateTokenLoginData()
        transparentNavigation()
        supportActionBar?.show()
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))
        setTitle("Laporan Penarikan")
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

}