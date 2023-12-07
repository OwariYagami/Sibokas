package com.overdevx.sibokas_xml.data.getHistory

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class Data(
    val classroom: Classroom,
    val created_at: String,
    val id: Int,
    val status: Int,
    val student_id: Int,
    val time_in: String,
    val time_out: String
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun isToday(): Boolean {
        val currentDate = LocalDate.now()
        val checkInDate = LocalDate.parse(created_at.substring(0, 10))
        return currentDate == checkInDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isYesterday(): Boolean {
        val yesterdayDate = LocalDate.now().minusDays(1)
        val checkInDate = LocalDate.parse(created_at.substring(0, 10))
        return yesterdayDate == checkInDate
    }
}
