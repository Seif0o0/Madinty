package com.madinaty.app.data.response

import com.madinaty.app.data.model.OfferDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OffersResponse(
    val data: List<OfferDto>,
    @Json(name = "current_page") val currentPage: Int,
    @Json(name = "last_page") val totalPages: Int,
    @Json(name = "prev_page_url") val prev: String?,
    @Json(name = "next_page_url") val next: String?
)