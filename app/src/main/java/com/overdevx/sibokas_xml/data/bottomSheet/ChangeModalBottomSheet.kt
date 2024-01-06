package com.overdevx.sibokas_xml.data.bottomSheet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.dialog.LoadingDialog
import com.overdevx.sibokas_xml.databinding.ChangeBottomsheetLayoutBinding
import java.io.File

class ChangeModalBottomSheet() :
    BottomSheetDialogFragment() {
    // lateinit var binding: UploadBottomsheetLayoutBinding
    lateinit var binding: ChangeBottomsheetLayoutBinding
    private lateinit var loadingDialog: LoadingDialog
    var imageUri: Uri? = null
    var status: String = ""


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
        const val TAG = "ModalBottomSheet"


    }

    private var imageFile: File? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.4f)
            /** Set dim amount here (the dimming factor of the parent fragment) */

            /** IMPORTANT! Here we set transparency to dialog layer */
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChangeBottomsheetLayoutBinding.bind(
            inflater.inflate(
                R.layout.change_bottomsheet_layout,
                container
            )
        )
        loadingDialog= LoadingDialog(requireContext())
        binding.cvGal.setOnClickListener {
            pickImageFromGallery()
        }

        return binding.root

    }

    private fun pickImageFromGallery() {
        // Membuat intent untuk memilih gambar dari galeri
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Menentukan tipe konten yang ingin dipilih (semua jenis gambar)

        // Menjalankan intent untuk memilih gambar
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            loadingDialog.show()
            imageUri = data?.data
            if (imageUri != null) {

                val inputStream = requireActivity().contentResolver.openInputStream(imageUri!!)
                val cursor =
                    requireActivity().contentResolver.query(imageUri!!, null, null, null, null)
                cursor?.use { c ->
                    val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (c.moveToFirst()) {
                        val name = c.getString(nameIndex)
                        inputStream?.let { inputStream ->
                            val file = File(requireActivity().cacheDir, name)
                            val os = file.outputStream()
                            os.use {
                                inputStream.copyTo(it)
                            }
                            imageFile = file

                            // Simpan path file ke SharedPreferences
                            val preferences =
                                requireActivity().getPreferences(Context.MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("backgroundImagePath", file.absolutePath)
                            editor.apply()
                            val imageView = requireActivity().findViewById<ImageView>(R.id.iv_background)
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            imageView.setImageBitmap(bitmap)

                            dismiss()
                            // Tunda dismiss() selama 1 detik menggunakan Handler
                            Handler(Looper.getMainLooper()).postDelayed({
                                loadingDialog.dismiss()
                            }, 1000) // 1000 milidetik = 1 detik

                        }
                    }
                }
            }
        }
    }


}