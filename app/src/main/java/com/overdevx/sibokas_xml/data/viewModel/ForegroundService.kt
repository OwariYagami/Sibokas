package com.overdevx.sibokas_xml.data.viewModel

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.ui.dashboard.ClassroomActivity

class ForegroundService : Service() {

    private val TAG = "ForegroundService"
    private val CHANNEL_ID = "ForegroundServiceChannel"
    private val FOREGROUND_NOTIFICATION_ID = 1
    private var countDownTimer: CountDownTimer? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Starting timer...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val waktuselisih = sharedPreferences.getLong("time", 30000)

        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification())

        countDownTimer = object : CountDownTimer(waktuselisih, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "Countdown seconds remaining: ${millisUntilFinished / 1000}")
                // Broadcast your countdown
                sendBroadcast(Intent(COUNTDOWN_BR).putExtra("countdown", millisUntilFinished))
            }

            override fun onFinish() {
                stopForeground(true)
                stopSelf()
            }
        }.start()
    }

    private fun buildForegroundNotification(): Notification {
        val notificationIntent = Intent(this, ClassroomActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Countdown is running...")
            .setSmallIcon(R.drawable.ic_notif)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        val COUNTDOWN_BR = "com.overdevx.sibokas_xml.data.viewModel"
    }
}
