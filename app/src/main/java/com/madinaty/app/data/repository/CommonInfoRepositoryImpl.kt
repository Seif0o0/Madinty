package com.madinaty.app.data.repository

import android.app.Application
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toCommon
import com.madinaty.app.data.services.CommonInfoService
import com.madinaty.app.domain.model.CommonInfo
import com.madinaty.app.domain.repository.CommonInfoRepository
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class CommonInfoRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: CommonInfoService
) : CommonInfoRepository {
    override fun fetchCommonInfo(): Flow<DataState<List<CommonInfo>>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.fetchCommonInfo()
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data.map { it.toCommon() }))
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