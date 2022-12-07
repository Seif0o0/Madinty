package com.madinaty.app.data.response

import com.madinaty.app.data.model.RegionDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegionsResponse (
    val data: List<RegionDto>,
    val meta:ListMeta
)