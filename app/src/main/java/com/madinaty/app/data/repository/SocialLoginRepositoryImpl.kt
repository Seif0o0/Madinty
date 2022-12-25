package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import com.madinaty.app.R
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.repository.SocialLoginRepository
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class SocialLoginRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService
) : SocialLoginRepository {
    override suspend fun socialLogin(
        provider: String,
        accessToken: String
    ): Flow<DataState<PhoneLoginInfoResponse>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.socialLogin(provider, accessToken)
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data!!.info))
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
            }catch (e:Exception){
                Log.d("SocialLoginRepo","${e.message}")
                Log.d("SocialLoginRepo","${e.javaClass}")
                emit(DataState.Error(message = e.message!!))
            }
        }
    }
}