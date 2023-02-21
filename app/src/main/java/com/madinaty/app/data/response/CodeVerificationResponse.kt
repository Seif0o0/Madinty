package com.madinaty.app.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CodeVerificationResponse(
    val data: CodeVerificationDataResponse
)

@JsonClass(generateAdapter = true)
class CodeVerificationDataResponse(
    val status: Boolean,
    val message: String,
    val info: CodeVerificationInfoResponse
)

@JsonClass(generateAdapter = true)
class CodeVerificationInfoResponse(val target: CodeVerificationInfoTargetResponse)

@JsonClass(generateAdapter = true)
class CodeVerificationInfoTargetResponse(
    @Json(name = "id") val userId: String
)

