package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val id: String,
    val name: String,
    var url:String
):Parcelable