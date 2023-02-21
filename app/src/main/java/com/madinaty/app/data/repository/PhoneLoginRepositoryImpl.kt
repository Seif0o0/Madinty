package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.madinaty.app.R
import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.data.services.AuthService
import com.madinaty.app.domain.model.User
import com.madinaty.app.domain.repository.PhoneLoginRepository
import com.madinaty.app.utils.Constants
import com.madinaty.app.utils.DataState
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PhoneLoginRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: AuthService,
    private val dataStore: DataStore<Preferences>
) : PhoneLoginRepository {

    override suspend fun login(
        phoneNumber: String, password: String
    ): Flow<DataState<PhoneLoginInfoResponse>> {
        return flow {
            try {
                emit(DataState.Loading())
                val response = service.login(phoneNumber = phoneNumber, password = password)
                if (response.isSuccessful) {
                    emit(DataState.Success(data = response.body()!!.data!!.info))
                } else {

                    when (response.code()) {
                        403 -> {
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
                        404 -> {
                            val body = response.errorBody()
                            val adapter =
                                Moshi.Builder().build().adapter(PhoneLoginResponse::class.java)
                            val errorParser = adapter.fromJson(body?.string() ?: "")
                            val errorMessage = errorParser?.message
                            emit(
                                DataState.Error(
                                    message = errorMessage
                                        ?: application.getString(R.string.user_not_found)
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

    companion object {
        private val userToken = stringPreferencesKey(Constants.TOKEN_PREF_KEY_NAME)
        private val userId = stringPreferencesKey(Constants.USER_ID_PREF_KEY_NAME)
        private val username = stringPreferencesKey(Constants.USERNAME_PREF_KEY_NAME)
        private val firstName = stringPreferencesKey(Constants.FIRST_NAME_PREF_KEY_NAME)
        private val lastName = stringPreferencesKey(Constants.LAST_NAME_PREF_KEY_NAME)
        private val email = stringPreferencesKey(Constants.EMAIL_PREF_KEY_NAME)
        private val city = stringPreferencesKey(Constants.CITY_PREF_KEY_NAME)
        private val phoneNumber = stringPreferencesKey(Constants.PHONE_NUMBER_PREF_KEY_NAME)
        private val gender = stringPreferencesKey(Constants.GENDER_PREF_KEY_NAME)
        private val dateOfBirth = stringPreferencesKey(Constants.DOB_PREF_KEY_NAME)
        private val isApproved = booleanPreferencesKey(Constants.IS_APPROVED_PREF_KEY_NAME)
        private val isVerified = booleanPreferencesKey(Constants.IS_VERIFIED_PREF_KEY_NAME)
    }

    override suspend fun saveUserInfo(userInfo: User, token: String): Boolean {
        return try {
            dataStore.edit { preferences ->
                preferences[userToken] = token
                preferences[userId] = userInfo.id
                preferences[username] = userInfo.username
                preferences[firstName] = userInfo.firstName
                preferences[lastName] = userInfo.lastName
                preferences[email] = userInfo.email
                preferences[city] = userInfo.city ?: ""
                preferences[phoneNumber] = userInfo.phoneNumber
                preferences[gender] = userInfo.gender
                preferences[dateOfBirth] = userInfo.dateOfBirth
                preferences[isApproved] = userInfo.isApproved
                preferences[isVerified] = userInfo.isVerified
            }
            true
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            false
        }

    }
}