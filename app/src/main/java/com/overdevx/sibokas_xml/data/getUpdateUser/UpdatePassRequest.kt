package com.overdevx.sibokas_xml.data.getUpdateUser

data class UpdatePassRequest(
    val old_password: String,
    val new_password: String,
    val password_confirmation: String
)

