package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.UserDto
import com.madinaty.app.domain.model.User
import com.madinaty.app.utils.Constants


fun UserDto.toUser() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email ?: "",
    phoneNumber = phoneNumber,
    gender = if (gender == null) {
        Constants.MALE_VALUE
    } else {
        if (gender == Constants.EDIT_MALE_VALUE) Constants.MALE_VALUE else if (gender == Constants.EDIT_FEMALE_VALUE) Constants.FEMALE_VALUE else gender
    },
    dateOfBirth = dateOfBirth,
    city = city,
    isApproved = isApproved == 1,
    isVerified = isVerified == 1
)