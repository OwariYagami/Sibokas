package com.overdevx.sibokas_xml.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.overdevx.sibokas_xml.MainActivity
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.API.ApiClient
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.API.TokenManager
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.data.getLogin.UserResponse
import com.overdevx.sibokas_xml.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var decryptedToken: String
    private var isPasswordVisible = false
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this@LoginActivity)
        binding.relativeInput1.translationX = 800f
        binding.relativeInput2.translationX = 800f
        binding.btnLogin.translationX = 800f

        binding.relativeInput1.alpha = 0f
        binding.relativeInput2.alpha = 0f
        binding.btnLogin.alpha = 0f
        binding.imageView.alpha = 0f

        binding.relativeInput1.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(300)
            .start()
        binding.relativeInput2.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(500)
            .start()
        binding.btnLogin.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(700)
            .start()
        binding.imageView.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(500)
            .start()
        TokenManager.generateKeyPair(this@LoginActivity)
        Token.redirectToMainActivityIfTokenExists(this@LoginActivity)

        binding.btnLogin.setOnClickListener {
            val email: String = binding.etEmailInput.text.toString()
            val pass: String = binding.etPassInput.text.toString()
            loadingDialog.show()
            ApiClient.retrofit.login(email, pass).enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        val userData = userResponse?.data

                        // enkripsi token
                        val token = userResponse!!.token
                        Token.saveEncryptedToken(this@LoginActivity, token)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        if (userData != null) {
                            Log.d("RESPONSE", userResponse.toString())
                            val userId = userData.id
                            val userName = userData.name
                            val userEmail = userData.email
                            saveUserResponse(userId, userName, userEmail, pass)
                            Toast.makeText(
                                this@LoginActivity,
                                "Login dengan nama : $userName $userEmail $pass $userId",
                                Toast.LENGTH_LONG
                            ).show()
                            loadingDialog.dismiss()

                        }
                    } else {
                        loadingDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    println(t)
                    loadingDialog.dismiss()
                    Toast.makeText(this@LoginActivity, "Gagal Login $t", Toast.LENGTH_SHORT).show()
                }

            })
        }

        binding.ivHide.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Ganti image src pada ImageView
            val imageResId = if (isPasswordVisible) {
                R.drawable.ic_eye_login
            } else {
                R.drawable.ic_eyeslash_login
            }
            binding.ivHide.setImageResource(imageResId)

            // Ubah tipe teks pada EditText untuk hide atau show password
            val transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            binding.etPassInput.transformationMethod = transformationMethod
        }

    }

    private fun saveUserResponse(id: Int, name: String, email: String, pass: String) {
        // Simpan path file ke SharedPreferences
        val preferences = this@LoginActivity.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("userId", id)
        editor.putString("userName", name)
        editor.putString("userEmail", email)
        editor.putString("userPass", pass)
        editor.apply()
    }

}