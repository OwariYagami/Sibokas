package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getDetailClassroom.Schedule

class ScheduleAdapter(private val context: Context, private val schedules: List<Schedule>) : RecyclerView.Adapter
<ScheduleAdapter.scheduleViewHolder>(){
    class scheduleViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView) {
        val time :MaterialTextView=itemView.findViewById(R.id.tv_schedule)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScheduleAdapter.scheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedules, parent, false)
        return scheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.scheduleViewHolder, position: Int) {
       val currentSchedule = schedules[position]
        holder.time.text="${currentSchedule.start_time} - ${currentSchedule.end_time} WIB"

    }



    override fun getItemCount(): Int {
       return schedules.size
    }

}
