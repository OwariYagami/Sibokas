package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getBuildingList.Buildings
import com.overdevx.sibokas_xml.data.getHistory.Data
import com.overdevx.sibokas_xml.ui.dashboard.ClassroomActivity
import de.hdodenhof.circleimageview.CircleImageView

class HistoryAdapter(private var historyList: List<Data>, requireContext: Context) :
    RecyclerView.Adapter<HistoryAdapter.historyViewHolder>() {
    private var filteredHistory: List<Data> = historyList
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
        val context: Context = itemView.context
    }

    override fun onBindViewHolder(holder: HistoryAdapter.historyViewHolder, position: Int) {
        val currentHistory = historyList[position]
        holder.className.text=currentHistory.classroom.name
        holder.classAlias.text=currentHistory.classroom.name_alias

    }

    override fun getItemCount(): Int {
        return historyList.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setFilter(category: String) {
        filteredHistory = when (category) {
            "all" -> historyList
            "today" -> historyList.filter { it.isToday() }
            "yesterday" -> historyList.filter { it.isYesterday() }
            else -> historyList
        }
        notifyDataSetChanged()
    }

    fun updateData(newData: List<Data>) {
        historyList = newData
        notifyDataSetChanged()
    }


}