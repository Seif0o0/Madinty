package com.madinaty.app.data.response

import com.madinaty.app.data.model.UserDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhoneLoginResponse(val data: PhoneLoginDataResponse?,val message:String?)

@JsonClass(generateAdapter = true)
data class PhoneLoginDataResponse(
    val status: Boolean,
    val message: String,
    val info: PhoneLoginInfoResponse
)

@JsonClass(generateAdapter = true)
data class PhoneLoginInfoResponse(val token: String, val user: UserDto)