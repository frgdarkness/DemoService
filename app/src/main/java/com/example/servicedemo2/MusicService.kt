package com.example.servicedemo2

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlin.random.Random

val TAG="DEMO123"

class MusicService :Service() {

    var rdNumber = 0
    var isRandom = false
    private val mbinder = MyServiceBinder()
    var musicPlayer = MediaPlayer()

    inner class MyServiceBinder : Binder(){
        fun getService() = this@MusicService
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG,"onBind")
        return mbinder
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG,"onReBind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"onUnBind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand, threadID: ${Thread.currentThread().id}")
        musicPlayer = MediaPlayer.create(this,R.raw.hymn_to_the_sea)
        isRandom = true
        Thread(Runnable {
            kotlin.run {
                startRandomNumber()
            }
        }).start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
        stopRandomNumber()
    }

    fun startRandomNumber(){
        while(isRandom){
            try {
                Thread.sleep(1000)
                if (isRandom) {
                    val rd = Random.nextInt(0, 100)
                    rdNumber = rd
                    Log.d(TAG, "Thread id: ${Thread.currentThread().id} - Random number: $rd")
                    Log.d(TAG,"Media: ${musicPlayer.isPlaying} - ${musicPlayer.currentPosition}")
                }
            }catch (e:InterruptedException){
                Log.d(TAG,"Interrrupted Exception")
            }
        }
    }

    fun getRandomNumber() = rdNumber

    fun stopRandomNumber(){
        isRandom = false
    }

    fun pause(){
        if(musicPlayer.isPlaying)
            musicPlayer.pause()
        else
            musicPlayer.start()
    }

    fun stop(){
        musicPlayer.stop()
    }

    fun start(){
        musicPlayer.start()
    }
}