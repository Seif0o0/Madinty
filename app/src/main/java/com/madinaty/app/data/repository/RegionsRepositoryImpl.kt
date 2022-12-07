package com.madinaty.app.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.madinaty.app.data.paging.RegionsPagingSource
import com.madinaty.app.data.services.RegionsService
import com.madinaty.app.domain.model.Region
import com.madinaty.app.domain.repository.RegionsRepository
import com.madinaty.app.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegionsRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: RegionsService
) : RegionsRepository {
    override fun fetchRegions(): Flow<PagingData<Region>> {
        return Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
            RegionsPagingSource(application, service)
        }.flow
    }
}