package com.overdevx.sibokas_xml.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.overdevx.sibokas_xml.R
import com.overdevx.sibokas_xml.data.getBuildingList.Buildings
import com.overdevx.sibokas_xml.ui.dashboard.ClassroomActivity
import de.hdodenhof.circleimageview.CircleImageView

class BuildingsAdapter(private var buildingList: List<Buildings>, requireContext: Context) :
    RecyclerView.Adapter<BuildingsAdapter.buildingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): buildingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_buildings, parent, false)
        return buildingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuildingsAdapter.buildingViewHolder, position: Int) {
        val currentBuilding = buildingList[position]
        holder.buildingsCode.text = currentBuilding.building_code + " ( ${currentBuilding.name} )"
        holder.buildingsCode.setOnClickListener {
            val intent = Intent(holder.context, ClassroomActivity::class.java)
            intent.putExtra("Building_id", currentBuilding.id)
            holder.context.startActivity(intent)
        }
        Glide.with(holder.context)
            .load(currentBuilding.photo)
            .placeholder(R.drawable.building)
            .into(holder.buildingsImage)



    }

    override fun getItemCount(): Int {
        return buildingList.size
    }

    class buildingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buildingsCode: TextView = itemView.findViewById(R.id.tv_buildingName)
        val buildingsImage: CircleImageView = itemView.findViewById(R.id.iv_buildingImage)
        val context: Context = itemView.context
    }

    fun updateData(newData: List<Buildings>) {
        buildingList = newData
        notifyDataSetChanged()
    }

    fun getData(): List<Buildings> {
        return buildingList
    }



}