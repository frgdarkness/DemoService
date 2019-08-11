package com.example.servicedemo2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var count = 0
    var intentService: Intent? = null
    var myService:MusicService? = null
    var connection: ServiceConnection? = null
    var isBound = false
    final val TAG = "DEMO123"
    val CHANNEL_ID = "Channel Music"
    val NOTI_ID = 200
    private var notiManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"Mainthread: ${Thread.currentThread().id}")
        intentService = Intent(this,MusicService::class.java)
        notiManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager?
        createNotiChannel()
        btnStart.setOnClickListener{
            startService(intentService)
        }

        btnStop.setOnClickListener{
            stopService(intentService)
        }

        btnBind.setOnClickListener{
            if(connection==null){
                connection = object : ServiceConnection {
                    override fun onServiceDisconnected(p0: ComponentName?) {
                        isBound = false
                    }

                    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                        val binder:MusicService.MyServiceBinder = p1 as MusicService.MyServiceBinder
                        myService = binder.getService()
                        createNoti()
                        isBound = true
                    }

                }
            }
            bindService(intentService, connection!!, Context.BIND_AUTO_CREATE)
        }

        btnUnBind.setOnClickListener{
            if(isBound){
                unbindService(connection!!)
                isBound = false
            }
        }

        btnGetRd.setOnClickListener{
            if(isBound){
                txtThread.text = "random number: ${myService?.rdNumber}"
            }
            else
                txtThread.text = "Service not bound"
        }

        btnStartMusic.setOnClickListener{
            if(!isBound){
                Toast.makeText(this,"Service not bound",Toast.LENGTH_SHORT).show()
            }else
                myService?.start()
        }

        btnStopMusic.setOnClickListener{
            if(!isBound){
                Toast.makeText(this,"Service not bound",Toast.LENGTH_SHORT).show()
            }else
                myService?.stop()
        }

        btnPauseMusic.setOnClickListener{
            if(!isBound){
                Toast.makeText(this,"Service not bound",Toast.LENGTH_SHORT).show()
            }else
                myService?.pause()
        }
    }

    fun createNotiChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Notification"
            val descriptionText = "Notification for MusicPlayer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.apply {
                description = descriptionText
            }
            notiManager?.createNotificationChannel(channel)
        }
        Log.d(TAG,"creatNotiChannel")
    }

    fun createNoti(){
        val noti = androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("title")
            .setContentText("noti content text")
            .setSmallIcon(R.drawable.ic_audio)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
            .build()

        myService?.startForeground(NOTI_ID,noti)    //service ko bi reset
        //notiManager?.notify(NOTI_ID,noti)
        Log.d(TAG,"Create Noti")
    }
}
