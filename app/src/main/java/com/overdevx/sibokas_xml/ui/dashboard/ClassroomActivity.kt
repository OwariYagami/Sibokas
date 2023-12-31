package com.overdevx.sibokas_xml.ui.dashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.ClassroomAdapter
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.getClassroomByBuilding.BuildingListResponse
import com.overdevx.sibokas_xml.data.getClassroomByBuilding.BuildingWithClassroomsResponse
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.getClassroomByBuilding.ClassroomList
import com.overdevx.sibokas_xml.databinding.ActivityClassroomBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClassroomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassroomBinding
    private lateinit var classroomAdapter: ClassroomAdapter
    private lateinit var classroomRecyclerView: RecyclerView
    private var allClassroom: List<ClassroomList> = emptyList()
    private lateinit var loadingDialog: LoadingDialog

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvHeaderclassroom.setOnClickListener {
            val fragment = DashboardFragment()
            // Use FragmentManager to replace the current fragment with the new one
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit()
        }
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Menetapkan fungsi kembali saat tombol kembali ditekan
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        loadingDialog= LoadingDialog(this@ClassroomActivity)
        classroomRecyclerView = binding.recyclerClassroom

        classroomAdapter = ClassroomAdapter(emptyList())
        classroomRecyclerView.layoutManager = LinearLayoutManager(this@ClassroomActivity)
        classroomRecyclerView.adapter = classroomAdapter

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }

        })
        binding.refresh.setOnRefreshListener {
            getClassroom()
            binding.refresh.isRefreshing=false
        }
        getClassroom()


    }
private fun getClassroom(){
    val buildingId = intent.getIntExtra("Building_id", 0)
    val token = Token.getDecryptedToken(this@ClassroomActivity)
    if (buildingId != 0) {
        loadingDialog.show()
        ApiClient.retrofit.getClassroomByBuilding("Bearer $token", buildingId)
            .enqueue(object : Callback<BuildingWithClassroomsResponse> {
                override fun onResponse(
                    call: Call<BuildingWithClassroomsResponse>,
                    response: Response<BuildingWithClassroomsResponse>
                ) {
                    if (response.isSuccessful) {
                        val buildingWithClassroomsResponse: BuildingWithClassroomsResponse? =
                            response.body()
                        val buildingListResponse: BuildingListResponse? =
                            buildingWithClassroomsResponse?.data
                        val classroomList = buildingListResponse?.classrooms ?: emptyList()
                        Log.d("API_CALL", "Response: $classroomList")
                        allClassroom = classroomList
                        classroomAdapter = ClassroomAdapter(allClassroom)
                        classroomRecyclerView.adapter = classroomAdapter
                        loadingDialog.dismiss()

                    }else{
                        loadingDialog.dismiss()
                        Log.d("FAILURE", response?.body().toString())
                    }
                }

                override fun onFailure(
                    call: Call<BuildingWithClassroomsResponse>,
                    t: Throwable
                ) {
                    loadingDialog.dismiss()
                    Log.d("FAILURE", t.message.toString())
                }

            })
    }
}
    private fun filterData(query: String?) {
        // Jika query kosong, tampilkan semua data
        if (query.isNullOrBlank()) {
            classroomAdapter.updateData(allClassroom)
            return
        }

        // Menggunakan filter untuk mencocokkan data yang sesuai dengan query
        val filteredClassroom = allClassroom.filter { classroom ->
            classroom.name.contains(query, ignoreCase = true)
            // Tambahkan kondisi sesuai dengan atribut-atribut yang ingin Anda cari
        }

        // Update data di dalam adapter dengan hasil filter
        classroomAdapter.updateData(filteredClassroom)
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



}