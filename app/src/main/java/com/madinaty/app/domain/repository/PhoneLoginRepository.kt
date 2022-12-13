package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.domain.model.User
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PhoneLoginRepository {
    suspend fun login(
        phoneNumber: String,
        password: String
    ): Flow<DataState<PhoneLoginInfoResponse>>

    suspend fun saveUserInfo(userInfo: User, token: String): Boolean
}