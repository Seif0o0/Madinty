package com.madinaty.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonInfoDto(
    @Json(name = "privacy") val privacyPolicy: String,
    @Json(name = "terms_and_conditions") val termsAndConditions: String,
    @Json(name = "about_us") val aboutUs: String
)