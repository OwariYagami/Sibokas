package com.overdevx.sibokas_xml.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding

class ModalBottomSheet : BottomSheetDialogFragment() {
     var scheduleText: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.booking_bottomsheet_layout, container, false)
       view.findViewById<TextView>(R.id.tv_detail_bottomsheet).text=scheduleText
        return view
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}