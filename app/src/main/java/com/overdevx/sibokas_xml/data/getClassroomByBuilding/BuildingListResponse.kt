package com.overdevx.sibokas_xml.data.getClassroomByBuilding

data class BuildingListResponse(
    val id: Int,
    val building_code: String,
    val name: String,
    val photo: String,
    val classrooms: List<ClassroomList>

)

data class BuildingWithClassroomsResponse(
    val data: BuildingListResponse
)