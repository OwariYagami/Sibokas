package com.overdevx.sibokas_xml.ui.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.BuildingsAdapter
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.getBuildingList.BuildingResponse
import com.overdevx.sibokas_xml.data.getBuildingList.Buildings
import com.overdevx.sibokas_xml.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var buildingsAdapter: BuildingsAdapter
    private lateinit var loadingDialog: LoadingDialog
    var TAG = "Main"
    private var countdownTimer: CountDownTimer? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var buildingRecyclerView: RecyclerView
    private var allBuildings: List<Buildings> = emptyList()
    val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadingDialog = LoadingDialog(requireContext())
        buildingRecyclerView = binding.recyclerHome

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
//        // Periksa apakah izin sudah diberikan
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Jika belum, minta izin
//            requestPermissions(
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
//            )
//        } else {
//            // Jika izin sudah diberikan, lanjutkan dengan mengatur latar belakang
//            setImageViewBackground()
//        }
        setImageViewBackground()
        setBookingInfo()
        return root
    }

    private fun filterData(query: String?) {
        // Jika query kosong, tampilkan semua data
        if (query.isNullOrBlank()) {
            buildingsAdapter.updateData(allBuildings)
            return
        }

        // Menggunakan filter untuk mencocokkan data yang sesuai dengan query
        val filteredBuildings = allBuildings.filter { building ->
            building.name.contains(query, ignoreCase = true) ||
                    building.building_code.contains(query, ignoreCase = true)
            // Tambahkan kondisi sesuai dengan atribut-atribut yang ingin Anda cari
        }

        // Update data di dalam adapter dengan hasil filter
        buildingsAdapter.updateData(filteredBuildings)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dan Adapter
        buildingsAdapter = BuildingsAdapter(emptyList(), requireContext())

        buildingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        buildingRecyclerView.adapter = buildingsAdapter
        binding.refresh.setOnRefreshListener {
            getBuildings()
            setBookingInfo()
            setImageViewBackground()
            binding.refresh.isRefreshing=false
        }

        getBuildings()

        // Ambil nilai endSch dari SharedPreferences
        val sharedPreferences =
            requireContext().getSharedPreferences("BookingPref", Context.MODE_PRIVATE)
        var endSch = sharedPreferences.getString("endtime", "")
        Log.d("END","$endSch")

        if (endSch != "") {
            // Hitung selisih waktu antara endSch dan waktu sekarang
            val currentTime = Calendar.getInstance()
            val endTime = Calendar.getInstance()
            endSch ="$endSch:00"
            val endTimeParts = endSch?.split(":")
            endTime.set(Calendar.HOUR_OF_DAY, endTimeParts?.get(0)?.toIntOrNull() ?: 0)
            endTime.set(Calendar.MINUTE, endTimeParts?.get(1)?.toIntOrNull() ?: 0)
            endTime.set(Calendar.SECOND, endTimeParts?.get(2)?.toIntOrNull() ?: 0)

            val timeDifferenceInMillis = endTime.timeInMillis - currentTime.timeInMillis
            // Buat dan jalankan Countdown Timer
            countdownTimer = object : CountDownTimer(timeDifferenceInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60

                    val formattedTime = String.format(
                        Locale.getDefault(),
                        "%02d:%02d:%02d",
                        hours % 24, minutes % 60, seconds % 60
                    )

                    binding.tvCount.text = formattedTime
                }

                override fun onFinish() {
                    // Countdown selesai, lakukan sesuatu (misalnya tampilkan pesan)
                    binding.tvCount.text = "Selesai"
                }
            }
            countdownTimer?.start()
        } else {
            binding.tvDesc.text = "Anda tidak sedang memesan kelas saat ini"
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan Countdown Timer saat fragment dihancurkan
        countdownTimer?.cancel()

    }

    private fun getBuildings() {
        val token = Token.getDecryptedToken(requireContext())
        val apiservice = ApiClient.retrofit
        loadingDialog.show()
        apiservice.getAllBuildings("Bearer $token")
            .enqueue(object : Callback<BuildingResponse> {
                override fun onResponse(
                    call: Call<BuildingResponse>,
                    response: Response<BuildingResponse>
                ) {
                    if (response.isSuccessful) {
                        val buildingResponse: BuildingResponse? = response.body()
                        val buildings = buildingResponse?.data ?: emptyList()
                        allBuildings = buildings

                        buildingsAdapter = BuildingsAdapter(allBuildings, requireContext())
                        buildingRecyclerView.adapter = buildingsAdapter
                        loadingDialog.dismiss()
                    } else {
                        loadingDialog.dismiss()
                    }
                }

                override fun onFailure(call: Call<BuildingResponse>, t: Throwable) {
                    Log.e("API_CALL", "Error: ${t.message}")
                    loadingDialog.dismiss()
                }


            })
    }

    // Metode untuk menangani hasil permintaan izin
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Izin diberikan, lanjutkan dengan mengatur latar belakang
//                    setImageViewBackground()
//                } else {
//                    // Izin ditolak, beri tahu pengguna atau ambil tindakan lain sesuai kebijakan aplikasi Anda
//                }
//            }
//        }
//    }

    private fun setImageViewBackground() {
        // Dalam onCreate atau metode lain yang sesuai
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val backgroundImagePath = preferences.getString("backgroundImagePath", null)
        val preferences2 = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userPhoto = preferences2.getString("userPhoto", null)
        val userName = preferences2.getString("userName", null)

        if (backgroundImagePath != null) {
            binding.tvUsername.text = userName
            val imageView = binding.ivBgHome
            val bitmap = BitmapFactory.decodeFile(backgroundImagePath)
            imageView.setImageBitmap(bitmap)
        } else {
            binding.ivBgHome.setBackgroundResource(R.drawable.bg_home)
        }

        if (userPhoto != null) {
            val imageView2 = binding.circleImageView
            val bitmap2 = BitmapFactory.decodeFile(userPhoto)
            imageView2.setImageBitmap(bitmap2)
        } else {
            binding.circleImageView.setImageResource(R.drawable.ic_nopp)
        }

    }

    private fun setBookingInfo() {
        val preferences =
            requireActivity().getSharedPreferences("BookingPref", Context.MODE_PRIVATE)
        val kelas = preferences.getString("kelas", "")
        val alias = preferences.getString("kelasAlias", "")
        val waktu = preferences.getString("waktu", "")
        val endTime = preferences.getString("endtime", "")

        if (waktu != "") {
            binding.constraintLayout8.visibility = View.VISIBLE
            binding.ivNodata.visibility = View.INVISIBLE
            binding.tvStatusDesc.visibility = View.INVISIBLE
            binding.tvBuild.text = kelas
            binding.tvDetailBuild.text = alias
            binding.tvDesc.text = "Segera keluar dari kelas jika waktu sudah habis"
            binding.tvTime.text = "$waktu WIB"
            binding.tvCount.text = endTime
        } else {
            binding.ivNodata.visibility = View.VISIBLE
            binding.tvCount.visibility = View.INVISIBLE
            binding.tvDesc.visibility = View.INVISIBLE
            binding.tvStatusDesc.text = "Anda tidak sedang memesan kelas saat ini"
            binding.btnCheckout.visibility = View.INVISIBLE
            binding.constraintLayout8.visibility = View.INVISIBLE
        }
    }


}