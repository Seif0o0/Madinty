package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.FavouriteDto
import com.madinaty.app.domain.model.Favourite

fun FavouriteDto.toFavourite() = Favourite(
    id = id,
    name = name,
    email = email,
    address = address,
    latitude = latitude,
    longitude = longitude,
    startTime = startTime,
    endTime = endTime,
    description = description,
    facebookUrl = facebookUrl,
    whatsAppNumber = whatsAppNumber,
    userId = userId,
    regionId = regionId,
    departmentId = departmentId,
    viewsCount = viewsCount ?: 0,
    rating = rating,
    isApproved = isApproved == 1,
)