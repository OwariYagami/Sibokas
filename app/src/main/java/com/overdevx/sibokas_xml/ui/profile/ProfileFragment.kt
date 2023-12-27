package com.overdevx.sibokas_xml.ui.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.AboutBottomSheet
import com.overdevx.sibokas_xml.data.ChangeModalBottomSheet
import com.overdevx.sibokas_xml.data.ChangepwModalBottomSheet
import com.overdevx.sibokas_xml.databinding.FragmentHomeBinding
import com.overdevx.sibokas_xml.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.btnNextHelp.setOnClickListener {
            val intent = Intent(requireContext(), ActivityHelp::class.java)
            startActivity(intent)
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            startActivity(intent)
        }

        binding.btnNextAbout.setOnClickListener {
            val aboutBottomSheet = AboutBottomSheet()
            aboutBottomSheet.show(requireActivity().supportFragmentManager, AboutBottomSheet.TAG)
        }

        binding.btnNextChange.setOnClickListener {
            val changeModalBottomSheet = ChangeModalBottomSheet()
            changeModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                ChangeModalBottomSheet.TAG
            )
        }

        binding.btnNextChangepw.setOnClickListener {
            val changepwModalBottomSheet = ChangepwModalBottomSheet()
            changepwModalBottomSheet.show(
                requireActivity().supportFragmentManager,
                ChangeModalBottomSheet.TAG
            )
        }

        setImageViewBackground()
        return root
    }

    private fun setImageViewBackground() {
        // Dalam onCreate atau metode lain yang sesuai
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val backgroundImagePath = preferences.getString("backgroundImagePath", null)
        val preferences2 = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userPhoto = preferences2.getString("userPhoto", null)
        val userName = preferences2.getString("userName", null)
        val userEmail = preferences2.getString("userEmail", null)

        if (backgroundImagePath != null) {
            binding.tvUsername.text=userName
            binding.tvEmail.text=userEmail
            val imageView = binding.ivBackground
            val bitmap = BitmapFactory.decodeFile(backgroundImagePath)
            imageView.setImageBitmap(bitmap)
        } else {
            binding.ivBackground.setBackgroundResource(R.drawable.bg_home)
        }

        if (userPhoto != null) {
            val imageView2 = binding.circleImageView
            val bitmap2 = BitmapFactory.decodeFile(userPhoto)
            imageView2.setImageBitmap(bitmap2)
        } else {
            binding.circleImageView.setImageResource(R.drawable.ic_nopp)
        }

    }

}