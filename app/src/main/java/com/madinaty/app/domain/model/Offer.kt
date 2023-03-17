package com.madinaty.app.domain.model


data class Offer(
    val id: String,
    val name: String,
    val dimensions: String,
    val departmentId: String,
    val viewsNumber: Int,
    val images: List<Attachment>
)