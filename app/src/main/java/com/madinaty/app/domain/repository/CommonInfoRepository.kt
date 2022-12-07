package com.madinaty.app.domain.repository

import com.madinaty.app.domain.model.CommonInfo
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CommonInfoRepository {
    fun fetchCommonInfo(): Flow<DataState<List<CommonInfo>>>
}