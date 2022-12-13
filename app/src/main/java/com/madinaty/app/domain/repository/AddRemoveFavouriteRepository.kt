package com.madinaty.app.domain.repository

import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface AddRemoveFavouriteRepository {
    fun addRemoveFavourite(
        placeId: String
    ): Flow<DataState<String>>
}