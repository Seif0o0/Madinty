package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.CodeVerificationDataResponse
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface OTPVerificationRepository {
    suspend fun verifyCode(
        code: String,
        userId: String
    ): Flow<DataState<CodeVerificationDataResponse>>
}