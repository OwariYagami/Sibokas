package com.overdevx.sibokas_xml.data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadModalBottomSheet : BottomSheetDialogFragment() {
    var scheduleText: String = ""
    var classroom_id: Int = 0
    var status: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upload_bottomsheet_layout, container, false)


        val token = Token.getDecryptedToken(requireContext())
        Log.d("CEK","$classroom_id : $token")

        return view
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}