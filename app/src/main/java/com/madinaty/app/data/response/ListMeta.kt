package com.madinaty.app.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ListMeta(
    @Json(name = "current_page") val currentPage: Int,
    @Json(name = "to") val next: Int?,
    @Json(name = "total") val totalPages: Int
)