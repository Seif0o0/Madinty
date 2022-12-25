package com.madinaty.app.data.response

import com.madinaty.app.data.model.PlaceDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlacesResponse(
    val data: List<PlaceDto>,
    val meta: ListMeta
)