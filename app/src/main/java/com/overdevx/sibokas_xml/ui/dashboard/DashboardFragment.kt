package com.overdevx.sibokas_xml.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.adapter.BuildingsAdapter
import com.overdevx.sibokas_xml.data.ApiClient
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var buildingRecyclerView: RecyclerView
    private var allBuildings: List<Buildings> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
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

        buildingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        buildingRecyclerView.adapter = buildingsAdapter

        // Panggil fungsi untuk mendapatkan data building
        getBuildings()

    }

    private fun getBuildings() {
        val token = Token.getDecryptedToken(requireContext())
        val apiservice = ApiClient.retrofit
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
                    } else {
                        // Handle respons tidak sukses
                    }
                }

                override fun onFailure(call: Call<BuildingResponse>, t: Throwable) {
                    Log.e("API_CALL", "Error: ${t.message}")
                }


            })
    }

    private fun retrieveTokenFromStorage(): String? {
        // Mengambil token dari SharedPreferences
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}