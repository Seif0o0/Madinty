package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DepartmentChild(
    val id: String,
    val name: String,
    val departmentId: String,
    val childs: List<DepartmentChild>?
):Parcelable