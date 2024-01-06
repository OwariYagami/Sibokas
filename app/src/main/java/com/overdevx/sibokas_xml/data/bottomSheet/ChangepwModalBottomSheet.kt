package com.overdevx.sibokas_xml.data.bottomSheet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.dialog.SuccessDialog
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.getUpdateUser.ChangePassResponse
import com.overdevx.sibokas_xml.data.getUpdateUser.UpdatePassRequest
import com.overdevx.sibokas_xml.databinding.ChangepwModalBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangepwModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: ChangepwModalBottomsheetLayoutBinding
    private var isPasswordVisible = false
    private lateinit var preferences:SharedPreferences
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
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
        loadingDialog= LoadingDialog(requireContext())
        successDialog= SuccessDialog(requireContext())
        successDialog.desc="Password Berhasil Di Perbarui"
        preferences = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val pass = preferences.getString("userPass", null)

        binding.etPassOld.setText(pass)

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
            val transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            binding.etPassOld.transformationMethod = transformationMethod
        }

        binding.btnHideNew.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Ganti ikon tombol
            val iconDrawable = if (isPasswordVisible) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eyeslash)
            }
            binding.btnHideNew.icon = iconDrawable


            // Ubah tipe teks pada EditText untuk hide atau show password
            val transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            binding.etPassNew.transformationMethod = transformationMethod
        }

        binding.btnHideConfirm.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Ganti ikon tombol
            val iconDrawable = if (isPasswordVisible) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_eyeslash)
            }
            binding.btnHideConfirm.icon = iconDrawable


            // Ubah tipe teks pada EditText untuk hide atau show password
            val transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            binding.etPassConfirm.transformationMethod = transformationMethod
        }

        binding.btnUpdate.setOnClickListener {
            updatePass()
        }
        return binding.root
    }

    fun updatePass() {
        val token = Token.getDecryptedToken(requireContext())
        val id = preferences.getInt("userId",0)
        val old = binding.etPassOld.text.toString()
        val new = binding.etPassNew.text.toString()
        val confirm = binding.etPassConfirm.text.toString()
        val request = UpdatePassRequest(old,new,confirm)
        loadingDialog.show()
        ApiClient.retrofit.changePass("Bearer $token",id,request)
            .enqueue(object : Callback<ChangePassResponse>{
                override fun onResponse(
                    call: Call<ChangePassResponse>,
                    response: Response<ChangePassResponse>
                ) {
                    if(response.isSuccessful){
                        val response = response?.body()?.data
                        saveUserResponse(confirm)
                        loadingDialog.dismiss()
                        dismiss()
                        successDialog.show()
                    }else{
                        Log.e("API_CALL",response.message().toString())
                    }
                }

                override fun onFailure(call: Call<ChangePassResponse>, t: Throwable) {
                  Log.e("API_CALL",t.message.toString())
                }

            })
    }
    private fun saveUserResponse(pass: String) {
        // Simpan path file ke SharedPreferences
        val preferences = requireActivity().getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("userPass", pass)
        editor.apply()
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}