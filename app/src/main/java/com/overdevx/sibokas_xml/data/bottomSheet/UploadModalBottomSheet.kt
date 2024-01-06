package com.overdevx.sibokas_xml.data.bottomSheet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.databinding.UploadBottomsheetLayoutBinding
import com.overdevx.sibokas_xml.ui.notifications.CameraActivity
import java.io.File

class UploadModalBottomSheet(uploadListener: UploadDialogListener) :
    BottomSheetDialogFragment() {
   // lateinit var binding: UploadBottomsheetLayoutBinding
    lateinit var binding: UploadBottomsheetLayoutBinding
    var imageUri: Uri? = null
    var status: String = ""
    private var mBottomSheetListener2: UploadDialogListener? = null

    init {
        this.mBottomSheetListener2 = uploadListener
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
        const val TAG = "ModalBottomSheet"


    }

    interface UploadDialogListener {
        fun onImageSelected(imageUri: File)
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
        binding = UploadBottomsheetLayoutBinding.bind(
            inflater.inflate(
                R.layout.upload_bottomsheet_layout,
                container
            )
        )

        binding.cvTake.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
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
                            mBottomSheetListener2?.onImageSelected(imageFile!!)

                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /** attach listener from parent fragment */
        try {
            mBottomSheetListener2 = context as UploadDialogListener?
        } catch (e: ClassCastException) {
        }
    }

}