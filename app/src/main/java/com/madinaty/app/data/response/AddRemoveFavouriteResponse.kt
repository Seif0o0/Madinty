package com.madinaty.app.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddRemoveFavouriteResponse(
    val data: AddRemoveFavouriteDataResponse?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class AddRemoveFavouriteDataResponse(
    val message: String
)

