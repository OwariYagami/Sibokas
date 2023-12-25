package com.overdevx.sibokas_xml.ui.home

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.adapter.HistoryAdapter
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.LoadingDialog
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.data.getHistory.Classroom
import com.overdevx.sibokas_xml.data.getHistory.Data
import com.overdevx.sibokas_xml.data.getHistory.HistoryResponse
import com.overdevx.sibokas_xml.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var historyrRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var allHistory: List<Data> = emptyList()
    private lateinit var loadingDialog: LoadingDialog
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyrRecyclerView = binding.recyclerHistory

        loadingDialog = LoadingDialog(requireContext())

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyAdapter = HistoryAdapter(emptyList(), requireActivity().supportFragmentManager, requireContext())
        historyrRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyrRecyclerView.adapter = historyAdapter

        getHistory()

        val checkedFilter = binding.chipGroup.checkedChipId
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedFilter ->
            when (group.getTextChipChecked()) {
                "All" -> filterData(null)
                "Today" -> filterData(LocalDate.now())
                "Yesterday" -> filterData(LocalDate.now().minusDays(1))
                "On Booked" -> filterOnBooked()
            }
        }


    }

    fun ChipGroup.getTextChipChecked(): String {
        val selectedId: Int = this.checkedChipId
        return if (selectedId > -1) findViewById<Chip>(selectedId).text.toString() else ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterData(selectedDate: LocalDate?) {
        // Jika query kosong, tampilkan semua data
        if (selectedDate == null) {
            binding.tvNull.visibility = View.INVISIBLE
            binding.tvTitleEmpty.visibility = View.INVISIBLE
            historyAdapter.updateData(allHistory)
            return
        }

        // Filter data yang memiliki tanggal sama dengan selectedDate dan status == 1
        val filteredHistory = allHistory.filter { history ->
            val historyDate = LocalDate.parse(history.created_at.substring(0, 10))
            historyDate == selectedDate && history.status == 2
        }

        if (filteredHistory.isEmpty()) {
            binding.tvNull.visibility = View.VISIBLE
            binding.tvTitleEmpty.visibility = View.VISIBLE
            binding.tvNull.playAnimation()
        } else {
            binding.tvNull.visibility = View.INVISIBLE
            binding.tvTitleEmpty.visibility = View.INVISIBLE
            binding.tvNull.pauseAnimation()
        }

        // Update data di dalam adapter dengan hasil filter
        historyAdapter.updateData(filteredHistory)
    }

    private fun filterOnBooked() {
        // Filter data yang status == 1 (On Booked)
        val onBookedHistory = allHistory.filter { history ->
            history.status == 1
        }

        if (onBookedHistory.isEmpty()) {
            binding.tvNull.visibility = View.VISIBLE
            binding.tvTitleEmpty.visibility = View.VISIBLE
            binding.tvNull.playAnimation()
        } else {
            binding.tvNull.visibility = View.INVISIBLE
            binding.tvTitleEmpty.visibility = View.INVISIBLE
            binding.tvNull.pauseAnimation()
        }

        // Update data di dalam adapter dengan hasil filter
        historyAdapter.updateData(onBookedHistory)
    }
    private fun getHistory() {
        loadingDialog.show()
        val token = Token.getDecryptedToken(requireContext())
        ApiClient.retrofit.getHistory("Bearer $token").enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                if (response.isSuccessful) {
                    val historyResponse = response?.body()
                    val historyData = historyResponse?.data ?: emptyList()
                    allHistory = historyData.sortedBy { it.created_at }

                    historyAdapter = HistoryAdapter(allHistory,requireActivity().supportFragmentManager, requireContext())
                    historyrRecyclerView.adapter = historyAdapter

                    loadingDialog.dismiss()


                }else{
                    loadingDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Log.e("API_CALL", "Error: ${t.message}")
                loadingDialog.dismiss()
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}