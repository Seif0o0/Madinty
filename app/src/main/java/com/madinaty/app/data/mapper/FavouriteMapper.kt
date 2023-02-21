package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.FavouriteDto
import com.madinaty.app.domain.model.Favourite
import com.madinaty.app.domain.model.Place

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
    phoneNumber = phoneNumber,
    whatsAppNumber = whatsAppNumber,
    userId = userId,
    regionId = regionId,
    departmentId = departmentId,
    attachments = attachments.map { it.toAttachment() },
    viewsCount = viewsCount ?: 0,
    rating = rating,
    isApproved = isApproved == 1,
    department = department.toDepartment(),
    region = region.toRegion(),
)

fun Favourite.toPlace() = Place(
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
    phoneNumber = phoneNumber,
    whatsAppNumber = whatsAppNumber,
    userId = userId,
    regionId = regionId,
    departmentId = departmentId,
    attachments = attachments,
    viewsCount = viewsCount,
    rating = rating,
    isApproved = isApproved,
    department = department,
    region = region,
    isFavourite = true
)