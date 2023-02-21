package com.madinaty.app.data.response

import com.madinaty.app.data.model.UserDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterResponse(
    val data: RegisterDataResponse?, val message: String?
)

@JsonClass(generateAdapter = true)
class RegisterDataResponse(
    val status: Boolean, val message: String, val info: RegisterInfoResponse
)

@JsonClass(generateAdapter = true)
class RegisterInfoResponse(val token: String, @Json(name = "name") val user: UserDto)