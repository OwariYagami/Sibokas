package com.overdevx.sibokas_xml.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.databinding.ActivityBookingBinding

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Menetapkan fungsi kembali saat tombol kembali ditekan
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val classname = intent.getStringExtra("class_name")
        val classnamealias = intent.getStringExtra("class_namealias")

        binding.tvClassname.text="$classname"
        binding.tvClassalias.text="$classnamealias"

    }
}