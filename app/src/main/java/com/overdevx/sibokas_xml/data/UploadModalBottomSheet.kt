package com.overdevx.sibokas_xml.data

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import com.overdevx.sibokas_xml.ui.notifications.CameraActivity
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

        view.findViewById<MaterialCardView>(R.id.cv_take).setOnClickListener {
            val intent = Intent(requireContext(),CameraActivity::class.java)
            startActivity(intent)
        }
        return view
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}