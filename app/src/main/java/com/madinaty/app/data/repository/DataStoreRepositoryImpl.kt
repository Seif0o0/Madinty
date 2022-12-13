package com.madinaty.app.data.repository

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.madinaty.app.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    override suspend fun getStringValue(key: String, defaultValue: String): String {
        val prefKey = stringPreferencesKey(key)
        Log.d("Repo", "keys: ${prefKey.name}")
        val value = dataStore.data.first()
        Log.d("Repo", "value: ${value[prefKey] ?: defaultValue}")
        return ""
    }

    override suspend fun getBoolValue(key: String, defaultValue: Boolean): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return flow {
            dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                emit(preferences[prefKey] ?: defaultValue)
            }
        }
    }
}