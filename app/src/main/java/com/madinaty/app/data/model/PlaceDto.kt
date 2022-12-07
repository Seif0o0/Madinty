package com.madinaty.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class PlaceDto(
    val id: String,
    val name: String,
    val email: String?,
    val address: String,
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lng") val longitude: Double,
    @Json(name = "work_from") val startTime: String,
    @Json(name = "work_to") val endTime: String,
    val description: String,
    @Json(name = "facebook_url") val facebookUrl: String?,
    @Json(name = "whats_number") val whatsAppNumber: String?,
    @Json(name = "user_id") val userId: String,
    @Json(name = "region_id") val regionId: String,
    @Json(name = "department_id") val departmentId: String,
    val attachments: List<AttachmentDto>,
    @Json(name = "views_number") val viewsCount: Int?,
    val rating: Float,
    @Json(name = "is_approved") val isApproved: Int,/* 1 == true */
    val department: DepartmentDto,
    val region: RegionDto
)