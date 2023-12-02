package com.overdevx.sibokas_xml.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.ClassroomAdapter
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.BuildingListResponse
import com.overdevx.sibokas_xml.data.BuildingWithClassroomsResponse
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.databinding.ActivityClassroomBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassroomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassroomBinding
    private lateinit var classroomAdapter: ClassroomAdapter
    private lateinit var classroomRecyclerView: RecyclerView
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

      val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Menetapkan fungsi kembali saat tombol kembali ditekan
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        classroomRecyclerView=binding.recyclerClassroom

        classroomAdapter= ClassroomAdapter(emptyList())
        classroomRecyclerView.layoutManager=LinearLayoutManager(this@ClassroomActivity)
        classroomRecyclerView.adapter=classroomAdapter

        val buildingId = intent.getIntExtra("Building_id",0)
        val token = Token.getDecryptedToken(this@ClassroomActivity)
         if (buildingId != 0){
            ApiClient.retrofit.getClassroomByBuilding("Bearer $token",buildingId)
                .enqueue(object :Callback<BuildingWithClassroomsResponse>{
                    override fun onResponse(
                        call: Call<BuildingWithClassroomsResponse>,
                        response: Response<BuildingWithClassroomsResponse>
                    ) {
                       if(response.isSuccessful){
                           val buildingWithClassroomsResponse:BuildingWithClassroomsResponse?=response.body()
                           val buildingListResponse : BuildingListResponse?=buildingWithClassroomsResponse?.data
                           val classroomList = buildingListResponse?.classrooms ?: emptyList()
                           Log.d("API_CALL", "Response: $classroomList")
                           classroomAdapter =ClassroomAdapter(classroomList)
                            classroomRecyclerView.adapter=classroomAdapter

                       }
                    }

                    override fun onFailure(call: Call<BuildingWithClassroomsResponse>, t: Throwable) {
                       Log.d("FAILURE",t.message.toString())
                    }

                })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun retrieveTokenFromStorage(): String? {
        // Mengambil token dari SharedPreferences
        val sharedPreferences = this@ClassroomActivity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}