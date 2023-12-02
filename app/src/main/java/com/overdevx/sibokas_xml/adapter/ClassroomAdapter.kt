package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.ClassroomList
import com.overdevx.sibokas_xml.ui.dashboard.BookingActivity

class ClassroomAdapter(private val classroomList: List<ClassroomList>) :RecyclerView.Adapter<ClassroomAdapter.classroomViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): classroomViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_classroomlist,parent,false)
        return classroomViewHolder(itemView)
    }

    class classroomViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
       val classname:TextView = itemView.findViewById(R.id.tv_classroomname)
       val classalias:TextView = itemView.findViewById(R.id.tv_classroomalias)
       val classstatus:RelativeLayout = itemView.findViewById(R.id.rl_status)
        val context:Context = itemView.context
    }

    override fun onBindViewHolder(holder: classroomViewHolder, position: Int) {
        val currentClassroom = classroomList[position]
        if(currentClassroom.name_alias == ""){
            holder.classname.text=currentClassroom.name
            holder.classalias.text="Lab"
        }else{
            holder.classname.text=currentClassroom.name
            holder.classalias.text=currentClassroom.name_alias

        }

        holder.classname.setOnClickListener {
            val intent = Intent(holder.context,BookingActivity::class.java)
            intent.putExtra("class_name",currentClassroom.name)
            intent.putExtra("class_namealias",currentClassroom.name_alias)
            holder.context.startActivity(intent)
        }

        if(currentClassroom.status==2){
           holder.classstatus.visibility=View.VISIBLE
        }else
            holder.classstatus.visibility=View.INVISIBLE

    }

    override fun getItemCount(): Int {
        return classroomList.size
    }
}