package com.overdevx.sibokas_xml.ui.dashboard

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.graphics.BitmapFactory
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.BuildingsAdapter
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.LoadingDialog
import com.overdevx.sibokas_xml.data.getBuildingList.BuildingResponse
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.data.getBuildingList.Buildings
import com.overdevx.sibokas_xml.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var buildingsAdapter: BuildingsAdapter
    private lateinit var loadingDialog: LoadingDialog
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
        loadingDialog= LoadingDialog(requireContext())
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
        // Periksa apakah izin sudah diberikan
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika belum, minta izin
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            // Jika izin sudah diberikan, lanjutkan dengan mengatur latar belakang
            setImageViewBackground()
        }
        setImageViewBackground()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dan Adapter
        buildingsAdapter = BuildingsAdapter(emptyList(), requireContext())

        buildingRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        buildingRecyclerView.adapter = buildingsAdapter

        // Panggil fungsi untuk mendapatkan data building
        getBuildings()

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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin diberikan, lanjutkan dengan mengatur latar belakang
                    setImageViewBackground()
                } else {
                    // Izin ditolak, beri tahu pengguna atau ambil tindakan lain sesuai kebijakan aplikasi Anda
                }
            }
        }
    }
    private fun setImageViewBackground() {
        // Dalam onCreate atau metode lain yang sesuai
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val backgroundImagePath = preferences.getString("backgroundImagePath", null)
        val preferences2 = requireActivity().getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val userPhoto = preferences2.getString("userPhoto", null)
        val userName = preferences2.getString("userName", null)

        if (backgroundImagePath != null) {
            binding.tvUsername.text=userName
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
}