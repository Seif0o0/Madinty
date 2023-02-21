package com.madinaty.app.data.repository

import android.app.Application
import com.madinaty.app.R
import com.madinaty.app.data.response.AddPlaceResponse
import com.madinaty.app.data.services.PlacesService
import com.madinaty.app.domain.repository.UpdatePlaceRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException
import javax.inject.Inject

class UpdatePlaceRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: PlacesService
) : UpdatePlaceRepository {
    override fun editPlace(
        placeId: String,
        map: Map<String, RequestBody>,
        images: MutableList<MultipartBody.Part>?
    ): Flow<DataState<String>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.updatePlace(
                    token = "Bearer ${UserInfo.token}",
                    id = placeId,
                    map = map.toMutableMap(),
                    files = images
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    if (body.data != null) {
                        emit(DataState.Success(data = body.data.message))
                    } else {
                        emit(DataState.Error(message = body.message!!))
                    }
                } else {
                    when (response.code()) {
                        422, 500 -> {
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