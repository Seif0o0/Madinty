package com.madinaty.app.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.madinaty.app.data.paging.PlacesPagingSource
import com.madinaty.app.data.services.PlacesService
import com.madinaty.app.domain.model.Place
import com.madinaty.app.domain.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: PlacesService,
) : PlacesRepository {

    override fun fetchPlaces(departmentId: String): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = 8)) {
            PlacesPagingSource(application, service, departmentId)
        }.flow
    }

}