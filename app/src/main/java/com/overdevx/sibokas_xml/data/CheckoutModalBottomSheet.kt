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
import com.overdevx.sibokas_xml.data.getCheckout.CheckOutResponse
import com.overdevx.sibokas_xml.data.getCheckout.CheckoutRequest
import com.overdevx.sibokas_xml.databinding.BookingBottomsheetLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutModalBottomSheet : BottomSheetDialogFragment() {
    var classname: String = ""
    var classroom_id: Int = 0
    var booking_id: Int = 0
    var status: String = ""
    val checkoutRequest = CheckoutRequest(_method = "PUT", classroom_id = 11)

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var successDialog: SuccessDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.checkout_bottomsheet_layout, container, false)
        view.findViewById<TextView>(R.id.textView2).text = "CheckOut dari kelas $classname sekarang ?"
        loadingDialog= LoadingDialog(requireContext())
        successDialog= SuccessDialog(requireContext())
        successDialog.desc="Berhasil Checkout dari Kelas $classname"
        val token = Token.getDecryptedToken(requireContext())
        Log.d("CEK","$classroom_id : $token : $booking_id")
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            Log.d("BUTTON_CLICK", "Button Ya clicked")
            if(classroom_id!=0){
                loadingDialog.show()
                ApiClient.retrofit.checkOut("Bearer $token",booking_id,checkoutRequest)
                    .enqueue(object : Callback<CheckOutResponse>{
                        override fun onResponse(
                            call: Call<CheckOutResponse>,
                            response: Response<CheckOutResponse>
                        ) {
                            if(response.isSuccessful){
                                val checkOutResponse=response?.body()
                                Log.d("RESPONSE",checkOutResponse.toString())

                                Toast.makeText(requireContext(), "${checkOutResponse?.message}", Toast.LENGTH_SHORT).show()
                                status = checkOutResponse?.message.toString()
                                val checkinData=checkOutResponse?.data
                                loadingDialog.dismiss()
                                successDialog.show()
                                dismiss()


                            }else{
                                loadingDialog.dismiss()
                                Log.d("API_CALL",response?.message().toString())
                            }
                        }

                        override fun onFailure(call: Call<CheckOutResponse>, t: Throwable) {
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