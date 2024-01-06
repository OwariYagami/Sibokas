package com.overdevx.sibokas_xml.data.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.databinding.LogoutDialogLayoutBinding
import com.overdevx.sibokas_xml.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutDialog(context: Context) : Dialog(context) {
    private lateinit var binding: LogoutDialogLayoutBinding
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogoutDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Set dialog fullscreen
//        val layoutParams = WindowManager.LayoutParams()
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
//        window?.attributes = layoutParams
        setCancelable(false)
        loadingDialog= LoadingDialog(context)
        binding.btnNo.setOnClickListener {
            dismiss()
        }

        binding.btnYes.setOnClickListener {
            logout()
        }


    }

    private fun logout() {
        val token = Token.getDecryptedToken(context)
        loadingDialog.show()
        ApiClient.retrofit.logout("Bearer $token")
            .enqueue(object :Callback<Void>{
                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if(response.isSuccessful){
                        val preferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.clear()
                        editor.apply()
                        Token.deleteToken(context)
                        loadingDialog.dismiss()

                        val intent = Intent(context,LoginActivity::class.java)
                        context.startActivity(intent)
                        dismiss()
                    }else{
                        loadingDialog.dismiss()
                        Log.e("API_CALL",response.message())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    loadingDialog.dismiss()
                   Log.e("API_CALL",t.message.toString())
                }

            })
    }
}