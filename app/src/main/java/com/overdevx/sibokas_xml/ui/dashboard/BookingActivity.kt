package com.overdevx.sibokas_xml.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.ClassroomAdapter
import com.overdevx.sibokas_xml.adapter.ScheduleAdapter
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.LoadingDialog
import com.overdevx.sibokas_xml.data.ModalBottomSheet
import com.overdevx.sibokas_xml.data.SuccessDialog
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.data.getDetailClassroom.ClassroomDetails
import com.overdevx.sibokas_xml.data.getDetailClassroom.Data
import com.overdevx.sibokas_xml.data.getDetailClassroom.Schedule
import com.overdevx.sibokas_xml.databinding.ActivityBookingBinding
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Objects

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private lateinit var bottomBinding: BookingBottomsheetLayoutBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    private var startSch:String=""
    private var endSch:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomBinding=BookingBottomsheetLayoutBinding.inflate(layoutInflater)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        loadingDialog= LoadingDialog(this@BookingActivity)
        successDialog= SuccessDialog(this@BookingActivity)
        // Menetapkan fungsi kembali saat tombol kembali ditekan
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        scheduleRecyclerView=binding.recyclerSchedules

        scheduleAdapter= ScheduleAdapter(this@BookingActivity,emptyList())
        scheduleRecyclerView.layoutManager= LinearLayoutManager(this@BookingActivity)
        scheduleRecyclerView.adapter=scheduleAdapter

        val classname = intent.getStringExtra("class_name")
        val classnamealias = intent.getStringExtra("class_namealias")
        val classid = intent.getIntExtra("class_id",0)
        val token = Token.getDecryptedToken(this@BookingActivity)
        if(classid != 0){
            loadingDialog.show()
            ApiClient.retrofit.getClassroomDetailsById("Bearer $token",classid)
                .enqueue(object : Callback<ClassroomDetails>{
                    override fun onResponse(
                        call: Call<ClassroomDetails>,
                        response: Response<ClassroomDetails>
                    ) {
                        if(response.isSuccessful){
                            val classroomResponse:ClassroomDetails?=response.body()
                            val dataResponse:Data?=classroomResponse?.data
                            val schedule = dataResponse?.schedules
                            val currentDayOfWeek=Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                            val currentTime = Calendar.getInstance()
                            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                            val currentMinute = currentTime.get(Calendar.MINUTE)
                            val currentTimeInMinutes = (currentHour * 60) + currentMinute
                            val pic = dataResponse?.pic_room?.name
                            binding.tvPic.text="$pic"
                            val filterSchedule: List<Schedule>? = schedule?.filter {
                                 val startTimeInMinutes = convertTimeToMinutes(it.start_time)
                                 val endTimeInMinutes = convertTimeToMinutes(it.end_time)

                                //it.day_of_week == currentDayOfWeek && startTimeInMinutes <= currentTimeInMinutes && endTimeInMinutes >= currentTimeInMinutes
                                it.day_of_week == currentDayOfWeek - 1
                            }
                            Log.d("DAYOFWEEK","$currentDayOfWeek")
                           filterSchedule?.let {
                               scheduleAdapter =ScheduleAdapter(this@BookingActivity,filterSchedule)
                               scheduleRecyclerView.adapter=scheduleAdapter
                           }
                            filterSchedule?.forEach{schedule ->
                                startSch=schedule.start_time
                                endSch=schedule.end_time
                            }
                            loadingDialog.dismiss()
                            schedule?.let{
                                for (schedule in it) {
                                    Log.d("Schedule", "Day: ${schedule.day_of_week}, Start: ${schedule.start_time}, End: ${schedule.end_time}")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ClassroomDetails>, t: Throwable) {
                        loadingDialog.dismiss()
                        TODO("Not yet implemented")
                    }

                })
        }
        //bottomBinding.tvDetailBottomsheet.text="Booking Kelas $classname dari pukul $startSch - $endSch WIB ?"

        binding.tvName.text="$classname"
        binding.tvAlias.text="$classnamealias"
        binding.tvAlias.setOnClickListener {
          successDialog.show()
        }
        binding.btnPesan.setOnClickListener {
            Log.d("TEXT","Booking Kelas $classname dari pukul $startSch - $endSch WIB ?")
            val modalBottomSheet=ModalBottomSheet()
            modalBottomSheet.scheduleText="Booking Kelas $classname dari pukul $startSch - $endSch WIB ?"
            modalBottomSheet.classroom_id=classid


            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
            if(modalBottomSheet.status!=""){
                modalBottomSheet.dismiss()
            }
        }


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

}