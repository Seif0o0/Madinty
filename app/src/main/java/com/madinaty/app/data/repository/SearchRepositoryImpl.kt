package com.madinaty.app.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.madinaty.app.data.paging.PlacesPagingSource
import com.madinaty.app.data.services.PlacesService
import com.madinaty.app.domain.model.Place
import com.madinaty.app.domain.repository.PlacesRepository
import com.madinaty.app.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val application: Application, private val service: PlacesService
) : PlacesRepository {
    override fun fetchPlaces(query: String?, departmentId: String?): Flow<PagingData<Place>> {
        return Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
            PlacesPagingSource(application = application,
                service = service,
                departmentId = departmentId,
                query = query)
        }.flow
    }
}