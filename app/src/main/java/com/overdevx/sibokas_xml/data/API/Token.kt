package com.overdevx.sibokas_xml.data.API

import android.content.Context
import android.content.Intent
import android.util.Base64
import com.overdevx.sibokas_xml.MainActivity

object Token {


    private const val ENCRYPTED_TOKEN_KEY = "encrypted_token"

    fun saveEncryptedToken(context: Context, token: String) {
        val encryptedToken = TokenManager.encryptText(token)
        val encodedToken = Base64.encodeToString(encryptedToken, Base64.DEFAULT)

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(ENCRYPTED_TOKEN_KEY, encodedToken)
        editor.apply()
    }

    fun getDecryptedToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val encryptedTokenString = sharedPreferences.getString(ENCRYPTED_TOKEN_KEY, null)

        return if (encryptedTokenString != null) {
            val encryptedTokenBytes = Base64.decode(encryptedTokenString, Base64.DEFAULT)
            TokenManager.decryptData(encryptedTokenBytes)
        } else {
            null
        }
    }
    fun deleteToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(ENCRYPTED_TOKEN_KEY)
        editor.apply()
    }
    fun redirectToMainActivityIfTokenExists(context: Context) {
        val decryptedToken = getDecryptedToken(context)
        if (decryptedToken != null) {
            // Token terdekripsi tidak null, arahkan ke MainActivity
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}

