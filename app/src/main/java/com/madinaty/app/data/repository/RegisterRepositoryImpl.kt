package com.madinaty.app.data.repository

import android.app.Application
import com.madinaty.app.R
import com.madinaty.app.data.response.AddPlaceResponse
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.repository.RegisterRepository
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService
) : RegisterRepository {

    override suspend fun register(map: Map<String, String>): Flow<DataState<PhoneLoginInfoResponse>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.register(map)
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data!!.info))
                } else {
                    when (response.code()) {
                        422 -> {
                            val body = response.errorBody()
                            val adapter =
                                Moshi.Builder().build().adapter(AddPlaceResponse::class.java)
                            val errorParser = adapter.fromJson(body?.string() ?: "")
                            val errorMessage = errorParser?.message
                            emit(
                                DataState.Error(
                                    message = errorMessage
                                        ?: application.getString(R.string.server_error_message)
                                )
                            )
                        }
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