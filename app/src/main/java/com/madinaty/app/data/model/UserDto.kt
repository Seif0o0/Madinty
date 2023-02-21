package com.madinaty.app.data.model

import com.madinaty.app.utils.Constants
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val username: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    val email: String?,
    @Json(name = "email_verified_at") val emailVerificationDate: String?,
    @Json(name = "mobile") val phoneNumber: String,
    val gender: String? ,
    @Json(name = "dob") val dateOfBirth: String,
    val city: String?,
    @Json(name = "is_approved") val isApproved: Int,/* 0 false */
    @Json(name = "is_verified") val isVerified: Int
)