package com.madinaty.app.data.mapper

import com.madinaty.app.data.model.RegionDto
import com.madinaty.app.domain.model.Region


fun RegionDto.toRegion() = Region(
    id=id,
    name = name
)