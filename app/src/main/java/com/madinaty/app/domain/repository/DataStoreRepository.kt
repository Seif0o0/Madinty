package com.madinaty.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun getStringValue(key: String, defaultValue: String): String
    suspend fun getBoolValue(key: String, defaultValue: Boolean): Flow<Boolean>
}