package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.OffersResponse
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PinOffersRepository {
    fun fetchPinOffers(): Flow<DataState<List<Offer>>>
}