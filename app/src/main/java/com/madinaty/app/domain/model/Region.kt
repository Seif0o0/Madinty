package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Region(
    val id: String,
    val name: String,
):Parcelable