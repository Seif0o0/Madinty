package com.madinaty.app.data.response

import com.madinaty.app.data.model.FavouriteDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class FavouritesResponse(
    @Json(name = "incoming_advertisement_requests") val favourite: FavouriteDto
)