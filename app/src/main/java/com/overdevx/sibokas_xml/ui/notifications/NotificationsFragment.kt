package com.overdevx.sibokas_xml.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.bottomSheet.UploadModalBottomSheet
import com.overdevx.sibokas_xml.data.getHistory.HistoryResponse
import com.overdevx.sibokas_xml.data.getReport.UploadResponse
import com.overdevx.sibokas_xml.databinding.FragmentNotificationsBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NotificationsFragment : Fragment(), UploadModalBottomSheet.UploadDialogListener {

    private var _binding: FragmentNotificationsBinding? = null
    lateinit var dialog: UploadModalBottomSheet
    private lateinit var loadingDialog: LoadingDialog

    /** Listener for choose card */
    lateinit var listener2: UploadModalBottomSheet.UploadDialogListener
    var uriImage: MultipartBody.Part? = null
    lateinit var token: String
    var classId: Int = 0
    private var classroomIds: Map<String, Int> =
        emptyMap() // Global variable to store classroom_ids with names


    /** Listener for choose card */

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener2 = this
        /** set listener of BottomSheetDialogFragment */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loadingDialog = LoadingDialog(requireContext())
        token = Token.getDecryptedToken(requireContext()).toString()
        binding.cvImage.setOnClickListener {
            dialog = UploadModalBottomSheet(listener2)
            dialog.show(
                requireActivity().supportFragmentManager,
                UploadModalBottomSheet.TAG
            )

        }

        getHistory(binding.menu.editText as AutoCompleteTextView)
        binding.btnSend.setOnClickListener {
            uploadReport()
        }

        return root
    }

    private fun getHistory(autoCompleteTextView: AutoCompleteTextView) {

        ApiClient.retrofit.getHistory("Bearer $token").enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                if (response.isSuccessful) {
                    val historyResponse = response.body()
                    val historyData = historyResponse?.data ?: emptyList()
                    // Extract the strings and classroom_ids from the API response
                    val items = historyData
                        .distinctBy { it.classroom.id }
                        .map { it.classroom.name }
                    classroomIds = historyData
                        .distinctBy { it.classroom.id }
                        .associateBy({ it.classroom.name }, { it.classroom.id })
                    // Create an ArrayAdapter and set it to the AutoCompleteTextView
                    val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                    autoCompleteTextView.setAdapter(adapter)

                    autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                        val selectedClassroomName = adapter.getItem(position)
                        val selectedClassroomId = classroomIds[selectedClassroomName]
                        if (selectedClassroomId != null) {
                            // Handle the selected classroom_id
                            classId = selectedClassroomId
                            // You can now use the selectedClassroomId as needed
                        }
                    }
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Log.e("API_CALL", "Error: ${t.message}")
            }
        })
    }

    private fun uploadReport() {
        val title = binding.menu.editText?.text.toString()
        val desc = binding.etPesan.text.toString()
        val titleRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "$title")
        val descRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "$desc")
        val classIdRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "$classId")
        loadingDialog.show()
        Log.d("BODY", "Data : $title,$desc,$classId")
        ApiClient.retrofit.report("Bearer $token", titleRequestBody, descRequestBody, classIdRequestBody, uriImage!!)
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {

                    if (response.isSuccessful) {
                        loadingDialog.dismiss()
                        val res = response?.body()
                        val message = res?.message.toString()

                        Snackbar.make(binding.sc, "$message", Snackbar.LENGTH_SHORT)
                            .show()
                        Log.d("REPONSE","$res,$message")

                    }else{
                        loadingDialog.dismiss()
                        Log.e("API_CALL", "Error: ${response?.message().toString()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    loadingDialog.dismiss()
                    Log.e("API_CALL", "Error: ${t.message}")
                }

            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onImageSelected(imageUri: File) {
        Glide.with(requireContext())
            .load(imageUri)
            .into(binding.ivPlaceholder)
        binding.ivReport.visibility = View.INVISIBLE
        binding.tvDescUpload.visibility = View.INVISIBLE
        val requestFile: RequestBody =
            RequestBody.create( MediaType.parse("image/*"), imageUri)

        val imagePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", imageUri.name, requestFile)

        uriImage = imagePart
        dialog.dismiss()
    }
}