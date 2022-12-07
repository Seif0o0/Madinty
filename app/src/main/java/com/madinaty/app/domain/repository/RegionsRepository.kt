package com.madinaty.app.domain.repository

import androidx.paging.PagingData
import com.madinaty.app.domain.model.Region
import kotlinx.coroutines.flow.Flow


interface RegionsRepository {
    fun fetchRegions(): Flow<PagingData<Region>>
}