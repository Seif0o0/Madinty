package com.madinaty.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DepartmentChildDto(
    val id: String,
    val name: String,
    @Json(name = "department_id") val departmentId: String,
    @Json(name = "children") val childs: List<DepartmentChildDto>?,
    @Json(name = "incoming_advertisement_requests") val places: List<PlaceDto>?
)