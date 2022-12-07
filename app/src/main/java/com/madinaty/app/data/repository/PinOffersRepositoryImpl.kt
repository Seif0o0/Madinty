package com.madinaty.app.data.repository

import android.app.Application
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toOffer
import com.madinaty.app.data.services.OffersService
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.domain.repository.PinOffersRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class PinOffersRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: OffersService
) : PinOffersRepository {

    override fun fetchPinOffers(): Flow<DataState<List<Offer>>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.fetchOffers(token = "Bearer ${UserInfo.token}")
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data.map { it.toOffer() }))
                } else {
                    when (response.code()) {
                        500 -> {
                            emit(DataState.Error(message = application.getString(R.string.server_error_message)))
                        }
                        else -> {
                            emit(DataState.Error(message = application.getString(R.string.something_went_wrong_try_again_later)))
                        }
                    }
                }
            } catch (e: IOException) {
                emit(DataState.Error(message = application.getString(R.string.no_internet_connection)))
            }
        }
    }
}








