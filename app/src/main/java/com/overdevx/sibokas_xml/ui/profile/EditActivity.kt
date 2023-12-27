package com.overdevx.sibokas_xml.ui.profile

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.ChangeModalBottomSheet
import com.overdevx.sibokas_xml.data.LoadingDialog
import com.overdevx.sibokas_xml.data.ProfileModalBottomSheet
import com.overdevx.sibokas_xml.data.SuccessDialog
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.data.getUpdateUser.UpdateUserRequest
import com.overdevx.sibokas_xml.data.getUpdateUser.UpdateUserResponse
import com.overdevx.sibokas_xml.databinding.ActivityEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog= LoadingDialog(this@EditActivity)
        successDialog= SuccessDialog(this@EditActivity)
        setImageViewBackground()
        val preferences = this@EditActivity.getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val username = preferences.getString("userName","").toString()
        val email = preferences.getString("userEmail","").toString()
        Toast.makeText(this@EditActivity, "$username $email", Toast.LENGTH_SHORT).show()
        binding.etUsernameInput.setText(username)
        binding.etEmailInput.setText(email)
        binding.btnGallery.setOnClickListener {
            val profileModalBottomSheet = ProfileModalBottomSheet()
            profileModalBottomSheet.show(supportFragmentManager, ProfileModalBottomSheet.TAG)
        }

        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        val token = Token.getDecryptedToken(this@EditActivity)
        var username = binding.etUsernameInput.text.toString()
        var email = binding.etEmailInput.text.toString()
        val preferences = this@EditActivity.getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val Id = preferences.getInt("userId", 0)
        val pass = preferences.getString("userPass", "")
        val updateRequest = UpdateUserRequest(username, email, pass!!)
        successDialog.desc="Update Profile Berhasil"
        loadingDialog.show()
        ApiClient.retrofit.editProfile("Bearer $token", Id, updateRequest)
            .enqueue(object : Callback<UpdateUserResponse> {
                override fun onResponse(
                    call: Call<UpdateUserResponse>,
                    response: Response<UpdateUserResponse>
                ) {
                    if (response.isSuccessful) {
                        val responsedata = response?.body()
                        val userdata = responsedata?.data

                        val email = userdata?.email
                        val username = userdata?.name
                        if (username != null && email != null) {
                            saveUserResponse(username, email)
                        }
                        loadingDialog.dismiss()
                        successDialog.show()
                    }else{
                        loadingDialog.dismiss()
                        Log.d("API_CALL",response?.message().toString())
                    }
                }

                override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                    Log.e("API_CALL",t.message.toString())
                }

            })

    }

    private fun saveUserResponse(name: String, email: String) {
        // Simpan path file ke SharedPreferences
        val preferences = this@EditActivity.getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("userName", name)
        editor.putString("userEmail", email)
        editor.apply()
    }

    private fun setImageViewBackground() {
        val preferences2 = this@EditActivity.getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val userPhoto = preferences2.getString("userPhoto", null)

        if ( userPhoto!=null) {
            val imageView2 = binding.ivProfile
            val bitmap2 = BitmapFactory.decodeFile(userPhoto)
            imageView2.setImageBitmap(bitmap2)
        }else{
            binding.ivProfile.setImageResource(R.drawable.ic_nopp)
        }

    }
}