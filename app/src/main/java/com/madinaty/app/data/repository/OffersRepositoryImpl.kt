package com.madinaty.app.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.madinaty.app.data.paging.OffersPagingSource
import com.madinaty.app.data.services.OffersService
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.domain.repository.OffersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OffersRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: OffersService
) : OffersRepository {
    override fun fetchOffers(): Flow<PagingData<Offer>> {
        return Pager(PagingConfig(pageSize = 8)) {
            OffersPagingSource(application, service)
        }.flow
    }
}