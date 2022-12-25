package com.madinaty.app.domain.repository

import androidx.paging.PagingData
import com.madinaty.app.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun fetchPlaces(query: String? = null, departmentId: String? = null): Flow<PagingData<Place>>
}