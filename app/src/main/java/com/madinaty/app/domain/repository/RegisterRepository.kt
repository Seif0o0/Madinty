package com.madinaty.app.domain.repository

import com.madinaty.app.data.response.PhoneLoginInfoResponse
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    suspend fun register(
        map: Map<String, String>
    ): Flow<DataState<PhoneLoginInfoResponse>>
}