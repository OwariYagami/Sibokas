package com.overdevx.sibokas_xml.data.viewModel

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.overdevx.sibokas_xml.MainActivity
import com.overdevx.sibokas_xml.R

import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    private val PERMISSION_REQUEST_CODE = 1

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            showNotification(context, "Times Up Bra", "Segera Tinggalkan Ruang Kelas")
        }
    }

    private fun showNotification(context: Context, title: String, content: String) {
        // Pastikan channel notifikasi sudah dibuat
        createNotificationChannel(context)
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, "notifAlarm")
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.VIBRATE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WAKE_LOCK
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Jika salah satu atau kedua izin belum diberikan, munculkan permintaan izin
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.VIBRATE,
                        Manifest.permission.WAKE_LOCK
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // Izin sudah diberikan, lanjutkan untuk menampilkan notifikasi
                notify(1, builder.build())
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notifAlarm",   // ID channel notifikasi
                "Alarm Cahnnel",    // Nama channel
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi untuk Alarm "
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        val classroomId = intent?.getIntExtra("classroom_id", 0) ?: 0
//        showNotification(context, classroomId)
//    }
//
//    private fun showNotification(context: Context?, classroomId: Int) {
//        val channelId = "default_channel_id"
//        val channelName = "Default Channel"
//        val notificationId = 1
//
//        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
//            .setSmallIcon(R.drawable.ic_notif)
//            .setContentTitle("Waktu telah habis!")
//            .setContentText("Kelas telah berakhir.")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setAutoCancel(true)
//
//        val resultIntent = Intent(context, MainActivity::class.java)
//        resultIntent.putExtra("classroom_id", classroomId)
//
//        val resultPendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            resultIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//        )
//
//        notificationBuilder.setContentIntent(resultPendingIntent)
//
//        // NotificationManager untuk menampilkan notifikasi
//        val notificationManager =
//            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Periksa versi Android dan sesuaikan konfigurasi notifikasi
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val channel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Tampilkan notifikasi
//        notificationManager.notify(notificationId, notificationBuilder.build())
//    }
//
//}
