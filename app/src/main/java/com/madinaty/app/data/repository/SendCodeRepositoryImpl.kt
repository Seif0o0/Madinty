package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import com.madinaty.app.R
import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.data.response.CodeVerificationDataResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.repository.SendCodeRepository
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class SendCodeRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService
) : SendCodeRepository {

    override suspend fun sendCode(phoneNumber: String): Flow<DataState<CodeVerificationDataResponse>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.sendCode(phoneNumber = phoneNumber)
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data))
                } else {

                    when (response.code()) {
                        500 -> {
                            val body = response.errorBody()
                            val adapter =
                                Moshi.Builder().build().adapter(PhoneLoginResponse::class.java)
                            val errorParser = adapter.fromJson(body?.string() ?: "")
                            val errorMessage = errorParser?.message
                            emit(
                                DataState.Error(
                                    message = errorMessage
                                        ?: application.getString(R.string.server_error_message)
                                )
                            )
                        }
                        else -> {
                            emit(DataState.Error(message = application.getString(R.string.something_went_wrong_try_again_later)))
                        }
                    }
                }

            } catch (e: IOException) {
                emit(DataState.Error(message = application.getString(R.string.no_internet_connection)))
            } catch (e: Exception) {
                Log.d("SendCodeRepo", "Error: ${e.message}")
                emit(DataState.Error(message = e.message ?: "Error"))
            }
        }
    }
}