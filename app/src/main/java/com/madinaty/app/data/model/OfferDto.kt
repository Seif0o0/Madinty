package com.madinaty.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OfferDto(
    val id: String,
    val name: String,
    val dimensions: String,
    @Json(name = "department_id") val departmentId: String,
    @Json(name = "attachment_id") val attachmentId: String,
    @Json(name = "views_number") val viewsNumber: Int,
    @Json(name = "attachment") val image: AttachmentDto
)