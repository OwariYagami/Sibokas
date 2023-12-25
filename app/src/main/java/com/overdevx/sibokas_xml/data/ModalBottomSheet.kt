package com.overdevx.sibokas_xml.data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModalBottomSheet : BottomSheetDialogFragment() {
    var scheduleText: String = ""
    var classroom_id: Int = 0
    var status: String = ""
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.booking_bottomsheet_layout, container, false)
        view.findViewById<TextView>(R.id.tv_detail_bottomsheet).text = scheduleText
        loadingDialog= LoadingDialog(requireContext())
        successDialog= SuccessDialog(requireContext())
        successDialog.desc="Berhasil CheckIn"
        val token = Token.getDecryptedToken(requireContext())
        Log.d("CEK","$classroom_id : $token")
        view.findViewById<Button>(R.id.btn_ya).setOnClickListener {
            Log.d("BUTTON_CLICK", "Button Ya clicked")
            if(classroom_id!=0){
                loadingDialog.show()
                ApiClient.retrofit.checkIn("Bearer $token",classroom_id)
                    .enqueue(object : Callback<CheckInResponse>{
                        override fun onResponse(
                            call: Call<CheckInResponse>,
                            response: Response<CheckInResponse>
                        ) {
                            if(response.isSuccessful){
                                val checkInResponse=response?.body()
                                Log.d("RESPONSE",checkInResponse.toString())

                                Toast.makeText(requireContext(), "${checkInResponse?.message}", Toast.LENGTH_SHORT).show()
                                status = checkInResponse?.message.toString()
                                val checkinData=checkInResponse?.data
                                loadingDialog.dismiss()
                                successDialog.show()
                                dismiss()


                            }else{
                                loadingDialog.dismiss()
                                Log.d("API_CALL",response?.message().toString())
                            }
                        }

                        override fun onFailure(call: Call<CheckInResponse>, t: Throwable) {
                            loadingDialog.dismiss()
                            Log.d("API_CALL",t.message.toString())
                        }

                    })
            }
        }

        return view
    }


    companion object {
        const val TAG = "ModalBottomSheet"
    }
}