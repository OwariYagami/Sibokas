package com.overdevx.sibokas_xml.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.adapter.ScheduleAdapter
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.bottomSheet.ModalBottomSheet
import com.overdevx.sibokas_xml.data.dialog.SuccessDialog
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.getDetailClassroom.ClassroomDetails
import com.overdevx.sibokas_xml.data.getDetailClassroom.Data
import com.overdevx.sibokas_xml.data.getDetailClassroom.Schedule
import com.overdevx.sibokas_xml.databinding.ActivityBookingBinding
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private lateinit var bottomBinding: BookingBottomsheetLayoutBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    private var startSch: String = ""
    private var endSch: String = ""
    private var selisih: Int = 0
    private var scheduleBooking: List<Schedule>? = emptyList()

    // Tetapkan properti global untuk menyimpan jadwal yang sesuai
    private var selectedSchedule: Schedule? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomBinding = BookingBottomsheetLayoutBinding.inflate(layoutInflater)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        loadingDialog = LoadingDialog(this@BookingActivity)
        successDialog = SuccessDialog(this@BookingActivity)
        // Menetapkan fungsi kembali saat tombol kembali ditekan
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        scheduleRecyclerView = binding.recyclerSchedules
        scheduleAdapter = ScheduleAdapter(this@BookingActivity, emptyList())
        scheduleRecyclerView.layoutManager = LinearLayoutManager(this@BookingActivity)
        scheduleRecyclerView.adapter = scheduleAdapter

        val classname = intent.getStringExtra("class_name")
        val classnamealias = intent.getStringExtra("class_namealias")
        val classid = intent.getIntExtra("class_id", 0)
        val token = Token.getDecryptedToken(this@BookingActivity)
        if (classid != 0) {
            loadingDialog.show()
            ApiClient.retrofit.getClassroomDetailsById("Bearer $token", classid)
                .enqueue(object : Callback<ClassroomDetails> {
                    val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

                    override fun onResponse(
                        call: Call<ClassroomDetails>,
                        response: Response<ClassroomDetails>
                    ) {
                        if (response.isSuccessful) {
                            val classroomResponse: ClassroomDetails? = response.body()
                            val dataResponse: Data? = classroomResponse?.data
                            val schedule = dataResponse?.schedules

                            val currentTime = Calendar.getInstance()
                            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = currentTime.get(Calendar.MINUTE)
                            val currentTimeInMinutes = (currentHour * 60) + currentMinute
                            val pic = dataResponse?.pic_room?.name
                            binding.tvPic.text = "$pic"
                            val filterSchedule: List<Schedule>? = schedule?.filter {
                                it.day_of_week == currentDayOfWeek - 1
                            }
                            Log.d("DAYOFWEEK", "$currentDayOfWeek")
                            filterSchedule?.let {
                                scheduleAdapter =
                                    ScheduleAdapter(this@BookingActivity, filterSchedule)
                                scheduleRecyclerView.adapter = scheduleAdapter
                                // Temukan jadwal sesuai dengan waktu sekarang
                                selectedSchedule = it.find { schedule ->
                                    schedule.day_of_week == currentDayOfWeek - 1 &&
                                            convertTimeToMinutes(schedule.start_time) > currentTimeInMinutes
                                }
                                val currentTimeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                    Date()
                                )
                                selectedSchedule?.let { schedule ->
                                    // Mendapatkan waktu sekarang dalam format HH:mm


                                    // Mengisi startSch dengan waktu sekarang dan endSch dengan waktu mulai jadwal yang sesuai
                                    startSch = currentTimeFormatted
                                    endSch = schedule.start_time
                                    Log.d(
                                        "Schedule",
                                        "Day: $startSch $endSch"
                                    )

                                }
                                if(selectedSchedule == null){
                                    endSch="20:35"
                                    startSch = currentTimeFormatted
                                }
                                loadingDialog.dismiss()
                                schedule?.let {
                                    for (schedule in it) {
                                        Log.d(
                                            "Schedule",
                                            "Day: ${schedule.day_of_week}, Start: ${schedule.start_time}, End: ${schedule.end_time}"
                                        )
                                    }
                                }

                            }
                        }
                    }


                    override fun onFailure(call: Call<ClassroomDetails>, t: Throwable) {
                        loadingDialog.dismiss()
                    }


                })
        }

        //bottomBinding.tvDetailBottomsheet.text="Booking Kelas $classname dari pukul $startSch - $endSch WIB ?"

        binding.tvName.text = "$classname"
        binding.tvAlias.text = "$classnamealias"
        binding.tvAlias.setOnClickListener {
            successDialog.show()
        }
        binding.btnPesan.setOnClickListener {
            // Jadwal yang sesuai ditemukan, tampilkan pesan
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.scheduleText =
                "Booking Kelas $classname dari pukul $startSch - $endSch WIB ?"
            modalBottomSheet.classroom_id = classid
            if (startSch != null && endSch != null) {
                modalBottomSheet.endTime = endSch
                modalBottomSheet.startTime = startSch
            }
            modalBottomSheet.selisih = selisih
            modalBottomSheet.classname = "$classname"
            modalBottomSheet.classalias = "$classnamealias"
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
            if (modalBottomSheet.status != "") {
                modalBottomSheet.dismiss()
            }
        }

    }

    private fun fillStartTimeWithCurrentTime(startSch: String): String {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)

        // Menyusun waktu saat ini dalam format "HH:mm"
        return String.format("%02d:%02d", currentHour, currentMinute)
    }

    private fun getScheduleTimeInMillis(time: String): Long {
        val calendar = Calendar.getInstance()
        val components = time.split(":")
        val hour = components[0].toInt()
        val minute = components[1].toInt()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }

    // Fungsi untuk memeriksa apakah jadwal sesuai dengan jam saat ini
    private fun scheduleMatchesCurrentTime(
        schedule: Schedule,
        currentTimeInMinutes: Int
    ): Boolean {
        val startTimeInMinutes = convertTimeToMinutes(schedule.start_time)
        val endTimeInMinutes = convertTimeToMinutes(schedule.end_time)

        return startTimeInMinutes <= currentTimeInMinutes && endTimeInMinutes >= currentTimeInMinutes
    }

    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        return if (parts.size == 3) {
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            val seconds = parts[2].toInt()
            (hours * 60) + (minutes) + (seconds / 60)
        } else {
            0 // Jika format tidak sesuai, kembalikan nilai default atau atur penanganan kesalahan yang sesuai
        }
    }

    // Fungsi untuk menghitung selisih waktu antara jam saat ini dan waktu berakhir
    private fun calculateTimeDifference(endTime: String): Int {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentTimeInMinutes = (currentHour * 60) + currentMinute

        val endTimeInMinutes = convertTimeToMinutes(endTime)

        return endTimeInMinutes - currentTimeInMinutes
    }


}