package com.madinaty.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DepartmentDto(
    val id: String,
    val name: String,
    @Json(name = "children") val departmentChilds: List<DepartmentChildDto>?,
)