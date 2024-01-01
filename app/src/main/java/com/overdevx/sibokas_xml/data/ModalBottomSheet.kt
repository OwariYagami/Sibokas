package com.overdevx.sibokas_xml.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.data.viewModel.AlarmReceiver
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ModalBottomSheet : BottomSheetDialogFragment() {
    private val PERMISSION_REQUEST_CODE=1
    var scheduleText: String = ""
    var classname: String = ""
    var classalias: String = ""
    var endTime: String = ""
    var startTime: String = ""
    var classroom_id: Int = 0
    var status: String = ""
    var selisih: Int =0
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.booking_bottomsheet_layout, container, false)
        view.findViewById<TextView>(R.id.tv_detail_bottomsheet).text = scheduleText
        loadingDialog= LoadingDialog(requireContext())
        successDialog= SuccessDialog(requireContext())
        successDialog.desc="Berhasil CheckIn"
        val token = Token.getDecryptedToken(requireContext())
        Log.d("CEK","$classroom_id : $token")
        view.findViewById<Button>(R.id.btn_ya).setOnClickListener {
            Log.d("BUTTON_CLICK", "Button Ya clicked")
            if(classroom_id!=0){
                loadingDialog.show()
                ApiClient.retrofit.checkIn("Bearer $token",classroom_id)
                    .enqueue(object : Callback<CheckInResponse>{
                        override fun onResponse(
                            call: Call<CheckInResponse>,
                            response: Response<CheckInResponse>
                        ) {
                            if(response.isSuccessful){
                                val checkInResponse=response?.body()
                                Log.d("RESPONSE",checkInResponse.toString())

                                Toast.makeText(requireContext(), "${checkInResponse?.message}", Toast.LENGTH_SHORT).show()
                                status = checkInResponse?.message.toString()
                                val checkinData=checkInResponse?.data
                                saveTimeDifference(selisih)
                                setAlarm(requireContext(),endTime)
                                saveInfoBooking(classname,classalias,"$startTime - $endTime",endTime)
                                loadingDialog.dismiss()
                                successDialog.show()
                                dismiss()


                            }else{
                                loadingDialog.dismiss()
                                Log.d("API_CALL",response?.message().toString())
                            }
                        }

                        override fun onFailure(call: Call<CheckInResponse>, t: Throwable) {
                            loadingDialog.dismiss()
                            Log.d("API_CALL",t.message.toString())
                        }

                    })
            }
        }

        return view
    }
    private fun saveInfoBooking(kelas: String,kelasalias:String, waktu: String, endTime:String) {
        // Simpan path file ke SharedPreferences
        val preferences = requireContext().getSharedPreferences("BookingPref",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("kelas", kelas)
        editor.putString("kelasAlias", kelasalias)
        editor.putString("waktu", waktu)
        editor.putString("endtime", endTime)
        editor.apply()
    }
//@SuppressLint("ScheduleExactAlarm")
//fun setAlarm(context: Context, endTime: String) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        val alarmManager = context.getSystemService(AlarmManager::class.java)
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        val endTimeMillis = convertTimeStringToMillis(endTime)
//        val clockInfo = AlarmManager.AlarmClockInfo(endTimeMillis, pendingIntent)
//
//        if (context.checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {
//            alarmManager.setAlarmClock(clockInfo, pendingIntent)
//        } else {
//            // Izin belum diberikan, meminta izin dari pengguna
//            val permissions = arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
//            ActivityCompat.requestPermissions(context as Activity, permissions, PERMISSION_REQUEST_CODE)
//        }
//    }
//}

    private fun convertTimeStringToMillis(timeString: String): Long {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault() // Sesuaikan dengan zona waktu yang sesuai

        try {
            val date = formatter.parse(timeString)
            return date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    private fun saveTimeDifference(selisih: Int) {
        // Simpan path file ke SharedPreferences
        val preferences =
            requireActivity().getSharedPreferences("BookingPref", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("selisih", selisih)
        editor.putBoolean("countdown_started", false)
        editor.apply()
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin diberikan, atur ulang alarm
                    setAlarm(requireContext(), endTime)
                } else {
                    Log.d("TIME","permisi")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context,time:String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        // Memisahkan string berdasarkan karakter ":"
        val timeParts = time.split(":")

        // Mendapatkan hour dan minute
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        // Set alarm
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE,minute)
        }

        // Use setExactAndAllowWhileIdle to ensure exact triggering even when the device is idle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            // For devices below Android 6.0, use setExact
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        Toast.makeText(context, "Checkout Sebelum $time", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "Alarm set")
    }
}