package com.overdevx.sibokas_xml.data

data class UserResponse(
    val type: String,
    val token: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)
