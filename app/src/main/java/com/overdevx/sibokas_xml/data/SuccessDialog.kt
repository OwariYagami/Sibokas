package com.overdevx.sibokas_xml.data

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.databinding.SuccessLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuccessDialog(context: Context) : Dialog(context) {
    private lateinit var binding: SuccessLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=SuccessLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        binding.btnOke.setOnClickListener {
            dismiss()
        }



    }
}