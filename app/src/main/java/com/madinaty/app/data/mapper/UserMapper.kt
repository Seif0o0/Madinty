package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.UserDto
import com.madinaty.app.domain.model.User

fun UserDto.toUser() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    gender = gender,
    dateOfBirth = dateOfBirth,
    city = city,
    isApproved = isApproved == 1,
    isVerified = isVerified == 1
)