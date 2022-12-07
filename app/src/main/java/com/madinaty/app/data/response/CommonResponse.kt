package com.madinaty.app.data.response

import com.madinaty.app.data.model.CommonInfoDto
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CommonResponse(
    val data: List<CommonInfoDto>
)