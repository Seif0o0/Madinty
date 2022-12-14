package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toMyPlace
import com.madinaty.app.data.mapper.toPlace
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.model.MyPlace
import com.madinaty.app.domain.model.Place
import com.madinaty.app.domain.repository.MyPlacesRepository
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MyPlacesRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService
) : MyPlacesRepository {

    override fun fetchMyPlaces(): Flow<DataState<List<MyPlace>>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.fetchMyPlaces(token = "Bearer ${UserInfo.token}")
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.map { it.toMyPlace() }))
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
            } catch (e: Exception) {
                Log.d("MyPlaces","Error: ${e.message}")
                emit(DataState.Error(message = "Error: ${e.message}"))
            }
        }
    }
}