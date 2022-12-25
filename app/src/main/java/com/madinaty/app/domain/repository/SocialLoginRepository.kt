package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface SocialLoginRepository {
    suspend fun socialLogin(
        provider: String,
        accessToken: String,
    ): Flow<DataState<PhoneLoginInfoResponse>>
}