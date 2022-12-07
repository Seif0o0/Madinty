package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val gender: String,
    val dateOfBirth: String,
    val city: String?,
    val isApproved: Boolean,
    val isVerified: Boolean
) : Parcelable