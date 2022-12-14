package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toUser
import com.madinaty.app.data.response.AddPlaceResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.ProfileRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService
) : ProfileRepository {
    override fun fetchProfileInfo(): Flow<DataState<User>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.fetchProfileInfo(token = "Bearer ${UserInfo.token}")
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data.toUser()))
                } else {
                    when (response.code()) {
                        401 -> {
                            //unAuth...
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
            } catch (e: Exception) {
                Log.d("ProfileRepo", "Error: ${e.message}")
            }
        }
    }

    override fun updateProfileInfo(map: Map<String, String>): Flow<DataState<User>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response =
                    service.editProfileInfo(token = "Bearer ${UserInfo.token}", map = map)
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data.toUser()))
                } else {
                    when (response.code()) {
                        500 -> {
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