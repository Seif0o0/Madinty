package com.madinaty.app.data.repository

import android.app.Application
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toFavourite
import com.madinaty.app.data.response.AddPlaceResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.model.Favourite
import com.madinaty.app.domain.repository.AddRemoveFavouriteRepository
import com.madinaty.app.domain.repository.FavouritesRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class AddRemoveFavouriteRepositoryImpl @Inject constructor(
    private val application: Application, private val service: AuthService
) : AddRemoveFavouriteRepository, FavouritesRepository {

    override fun addRemoveFavourite(placeId: String): Flow<DataState<String>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.addRemoveFavourite(
                    token = "Bearer ${UserInfo.token}", placeId = placeId
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

    override fun fetchFavourites(): Flow<DataState<List<Favourite>>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.fetchFavourites(token = "Bearer ${UserInfo.token}")
                if (response.isSuccessful) {
                    emit(
                        DataState.Success(data = response.body()!!
                            .map { it.favourite.toFavourite() })
                    )
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