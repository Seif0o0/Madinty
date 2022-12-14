package com.madinaty.app.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddPlaceResponse(
    val data: AddPlaceDataResponse?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class AddPlaceDataResponse(
    val message: String
)

