package com.madinaty.app.data.response

import com.madinaty.app.data.model.DepartmentDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DepartmentsResponse(
    val data: List<DepartmentDto>,
    val meta: ListMeta
)

