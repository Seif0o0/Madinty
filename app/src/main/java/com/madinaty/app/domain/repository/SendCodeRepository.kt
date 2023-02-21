package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.CodeVerificationDataResponse
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface SendCodeRepository {
    suspend fun sendCode(
        phoneNumber:String
    ):Flow<DataState<CodeVerificationDataResponse>>
}