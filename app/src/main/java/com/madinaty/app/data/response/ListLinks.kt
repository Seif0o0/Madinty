package com.madinaty.app.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListLinks(
    val prev: Int?,
    val next: Int?
)
