package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.bottomSheet.CheckoutModalBottomSheet
import com.overdevx.sibokas_xml.data.bottomSheet.ModalBottomSheet
import com.overdevx.sibokas_xml.data.getHistory.Data
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private var historyList: List<Data>, private val fragmentManager: FragmentManager, requireContext: Context) :
    RecyclerView.Adapter<HistoryAdapter.historyViewHolder>() {
    private var filteredHistory: List<Data> = historyList
    init {
        // Urutkan historyList berdasarkan tanggal sebelum ditampilkan
        historyList.sortedBy { it.created_at }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.historyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryAdapter.historyViewHolder(itemView)
    }

    class historyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val classImage:ImageView=itemView.findViewById(R.id.iv_classroom)
        val className:TextView=itemView.findViewById(R.id.tv_buildingName)
        val classAlias:TextView=itemView.findViewById(R.id.tv_classnamealias)
        val date:TextView=itemView.findViewById(R.id.tv_date)
        val card:MaterialCardView=itemView.findViewById(R.id.card_history)
        val context: Context = itemView.context
    }

    override fun onBindViewHolder(holder: HistoryAdapter.historyViewHolder, position: Int) {
        val currentHistory = historyList[position]
        holder.className.text=currentHistory.classroom.name
        holder.classAlias.text=currentHistory.classroom.name_alias
        val formattedDate=formatDate(currentHistory.created_at)

        if (position == 0 || formattedDate != formatDate(historyList[position - 1].created_at)) {
            holder.date.visibility = View.VISIBLE
            holder.date.text = formattedDate
        } else {
            holder.date.visibility = View.GONE
        }
        if(currentHistory.status==2){
            holder.card.isClickable=false
        }
        holder.card.setOnClickListener {
            if (currentHistory.status == 1) {
                val modalBottomSheet= CheckoutModalBottomSheet()
                modalBottomSheet.classname= currentHistory.classroom.name
                modalBottomSheet.classroom_id=currentHistory.classroom.id
                modalBottomSheet.booking_id=currentHistory.id
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
            }
        }

    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateData(newData: List<Data>) {
        historyList = newData
        notifyDataSetChanged()
    }
    private fun formatDate(inputDate: String): String {
        try {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return inputDate // Jika gagal, kembalikan tanggal asli
        }
    }


}