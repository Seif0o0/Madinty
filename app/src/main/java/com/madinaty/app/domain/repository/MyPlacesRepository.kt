package com.madinaty.app.domain.repository

import com.madinaty.app.domain.model.MyPlace
import com.madinaty.app.domain.model.Place
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface MyPlacesRepository {
    fun fetchMyPlaces(): Flow<DataState<List<MyPlace>>>
}