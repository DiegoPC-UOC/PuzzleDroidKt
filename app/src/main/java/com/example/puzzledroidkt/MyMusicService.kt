package com.example.puzzledroidkt

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MyMusicService : Service() {
    private val iBinder:IBinder?=null
    private lateinit var mp: MediaPlayer
    companion object {
        var isRuning = false
    }
    override fun onBind(intent: Intent): IBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        isRuning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mp=MediaPlayer.create(this,R.raw.no11)
        mp.isLooping=true
        mp.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.stop()
        mp.release()
        isRuning = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)

    }
}