package com.overdevx.sibokas_xml.data

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import com.overdevx.sibokas_xml.databinding.ChangeBottomsheetLayoutBinding
import com.overdevx.sibokas_xml.databinding.ChangepwModalBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangepwModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: ChangepwModalBottomsheetLayoutBinding
    private var isPasswordVisible = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChangepwModalBottomsheetLayoutBinding.bind(
            inflater.inflate(
                R.layout.changepw_modal_bottomsheet_layout,
                container
            )
        )



        binding.btnHide.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Ganti ikon tombol
            val iconDrawable = if (isPasswordVisible) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eyeslash)
            }
            binding.btnHide.icon = iconDrawable


            // Ubah tipe teks pada EditText untuk hide atau show password
            val transformationMethod = if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
            else PasswordTransformationMethod.getInstance()

            binding.etPassOld.transformationMethod = transformationMethod
        }

        return binding.root
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}