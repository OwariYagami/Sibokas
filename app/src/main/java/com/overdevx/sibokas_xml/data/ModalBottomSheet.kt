package com.overdevx.sibokas_xml.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.booking_bottomsheet_layout, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}