package com.overdevx.sibokas_xml.data.viewModel

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*

class BroadcastService : Service() {

    private val TAG = "BroadcastService"
    val COUNTDOWN_BR = "com.overdevx.sibokas_xml.data.viewModel"
    var intent = Intent(COUNTDOWN_BR)
    var countDownTimer: CountDownTimer? = null
    private lateinit var countdownViewModel: CountdownViewModel
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Starting timer...")
        val sharedPreferences = getSharedPreferences("BookingPref", MODE_PRIVATE)
        val waktuselisih = sharedPreferences.getLong("selisih2", 30000)

       object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "Countdown seconds remaining: ${millisUntilFinished / 1000}")
                intent.putExtra("countdown", millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {}
        }.start()


    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        val COUNTDOWN_BR = "com.overdevx.sibokas_xml.data.viewModel"

    }

}