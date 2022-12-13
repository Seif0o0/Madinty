package com.madinaty.app.domain.model

import com.squareup.moshi.Json


data class Favourite(
    val id: String,
    val name: String,
    val email: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val startTime: String,
    val endTime: String,
    val description: String,
    val facebookUrl: String?,
    val whatsAppNumber: String?,
    val userId: String,
    val regionId: String,
    val departmentId: String,
    val viewsCount: Int,
    val rating: Float,
    val isApproved: Boolean
)