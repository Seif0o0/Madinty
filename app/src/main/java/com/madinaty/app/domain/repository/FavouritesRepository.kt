package com.madinaty.app.domain.repository

import com.madinaty.app.domain.model.Favourite
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    fun fetchFavourites(): Flow<DataState<List<Favourite>>>
}