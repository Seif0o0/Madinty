package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.AttachmentDto
import com.madinaty.app.data.model.OfferDto
import com.madinaty.app.domain.model.Attachment
import com.madinaty.app.domain.model.Offer

fun AttachmentDto.toAttachment() = Attachment(
    id = id,
    name = name,
    url = url
)

fun OfferDto.toOffer() = Offer(
    id = id,
    name = name,
    dimensions = dimensions,
    departmentId = departmentId,
    viewsNumber = viewsNumber,
    images = images.map { it.toAttachment() },
)