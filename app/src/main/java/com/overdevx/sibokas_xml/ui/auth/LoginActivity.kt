package com.overdevx.sibokas_xml.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.overdevx.sibokas_xml.MainActivity
import com.overdevx.sibokas_xml.data.ApiClient
import com.overdevx.sibokas_xml.data.Token
import com.overdevx.sibokas_xml.data.TokenManager
import com.overdevx.sibokas_xml.data.getLogin.UserResponse
import com.overdevx.sibokas_xml.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var decryptedToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TokenManager.generateKeyPair(this@LoginActivity)
        Token.redirectToMainActivityIfTokenExists(this@LoginActivity)

        binding.btnLogin.setOnClickListener {
            val email: String = binding.etEmailInput.text.toString()
            val pass: String = binding.etPassInput.text.toString()

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
                        Token.saveEncryptedToken(this@LoginActivity,token)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        if (userData != null) {
                            Log.d("RESPONSE",userResponse.toString())
                            val userId = userData.id
                            val userName = userData.name
                            val userEmail = userData.email
                            saveUserResponse(userId,userName,userEmail,pass)
                            Toast.makeText(
                                this@LoginActivity,
                                "Login dengan nama : $userName $userEmail $pass $userId",
                                Toast.LENGTH_LONG
                            ).show()


                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    println(t)
                    Toast.makeText(this@LoginActivity, "Gagal Login $t", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun saveUserResponse(id:Int,name:String,email:String,pass:String){
        // Simpan path file ke SharedPreferences
        val preferences = this@LoginActivity.getSharedPreferences("UserPref",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("userId", id)
        editor.putString("userName", name)
        editor.putString("userEmail", email)
        editor.putString("userPass", pass)
        editor.apply()
    }

}