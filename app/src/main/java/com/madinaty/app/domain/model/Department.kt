package com.madinaty.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Department(
    val id: String,
    val name: String,
    val departmentChilds: List<DepartmentChild>?,
    val places: List<Place>?
) : Parcelable