package com.overdevx.sibokas_xml.data.getDetailClassroom

data class Data(
    val building: Building,
    val id: Int,
    val name: String,
    val name_alias: String,
    val photo: String,
    val pic_room: PicRoom,
    val schedules: List<Schedule>,
    val status: Int
)