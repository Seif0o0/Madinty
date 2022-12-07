package com.madinaty.app.data.response

import com.madinaty.app.data.model.PlaceDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlacesResponse (
    val data:List<PlaceDto>,
    @Json(name="current_page") val currentPage : Int,
    @Json(name="last_page") val totalPages :Int,
    @Json(name = "prev_page_url") val prev:String?,
    @Json(name = "next_page_url")val next :String?
)