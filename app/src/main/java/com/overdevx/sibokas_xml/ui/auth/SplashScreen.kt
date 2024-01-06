package com.overdevx.sibokas_xml.ui.auth

import androidx.appcompat.app.AppCompatActivity
import com.overdevx.sibokas_xml.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.overdevx.sibokas_xml.MainActivity
import com.overdevx.sibokas_xml.data.API.Token
import com.overdevx.sibokas_xml.data.API.TokenManager

class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 2000 // 2 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            // Intent ke aktivitas utama setelah SPLASH_TIME_OUT
            TokenManager.generateKeyPair(this@SplashScreen)
            Token.redirectToMainActivityIfTokenExists(this@SplashScreen)
            finish()
        }, SPLASH_TIME_OUT)
    }
}