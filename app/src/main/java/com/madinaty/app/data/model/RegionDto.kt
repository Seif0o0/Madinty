package com.madinaty.app.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegionDto(
    val id: String,
    val name: String,
)