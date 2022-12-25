package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.CommonInfoDto
import com.madinaty.app.domain.model.CommonInfo


fun CommonInfoDto.toCommon() = CommonInfo(
    privacyPolicy = privacyPolicy,
    termsAndConditions = termsAndConditions,
    aboutUs = aboutUs,
    whatsAppNumber = whatsAppNumber,
    phoneNumber = phoneNumber
)