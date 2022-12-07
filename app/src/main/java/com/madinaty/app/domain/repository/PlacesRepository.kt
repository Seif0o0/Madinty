package com.madinaty.app.domain.repository

import androidx.paging.PagingData
import com.madinaty.app.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    fun fetchPlaces(departmentId: String):Flow<PagingData<Place>>
}