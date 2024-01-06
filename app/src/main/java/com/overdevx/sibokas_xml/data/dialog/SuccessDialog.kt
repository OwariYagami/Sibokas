package com.overdevx.sibokas_xml.data.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.databinding.SuccessLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuccessDialog(context: Context) : Dialog(context) {
    private lateinit var binding: SuccessLayoutBinding
    var desc:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=SuccessLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set dialog width and height to match parent
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        window?.setLayout(width, height)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)

        binding.tvDescMsg.text=desc
        binding.btnOke.setOnClickListener {
            dismiss()
        }



    }
}