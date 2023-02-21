package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPlace(
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
    val phoneNumber: String?,
    val whatsAppNumber: String?,
    val userId: String,
    val regionId: String,
    val departmentId: String,
    val attachments: List<Attachment>,
    val viewsCount: Int,
    val rating: Float,
    val isApproved: Boolean,
    val department: Department,
    val region: Region
) : Parcelable