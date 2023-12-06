package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.overdevx.sibokas_xml.data.getDetailClassroom.Schedule

class ScheduleAdapter(private val context: Context, private val schedules: List<Schedule>) : BaseAdapter() {

    override fun getCount(): Int {
        return schedules.size
    }

    override fun getItem(position: Int): Any {
        return schedules[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val schedule = getItem(position) as Schedule
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(android.R.layout.simple_list_item_1, null)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        // Menampilkan informasi jadwal pada TextView
        textView.text = "Start: ${schedule.start_time}, End: ${schedule.end_time}"

        return view
    }
}
