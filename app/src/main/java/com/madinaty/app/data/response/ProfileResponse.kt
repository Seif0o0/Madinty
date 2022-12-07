package com.madinaty.app.data.response

import com.madinaty.app.data.model.UserDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    val data: UserDto
)