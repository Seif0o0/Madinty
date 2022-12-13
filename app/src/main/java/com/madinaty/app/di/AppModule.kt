package com.madinaty.app.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.madinaty.app.domain.model.Department
import com.madinaty.app.presentation.adapter.DepartmentsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            // invoked if corruptionException is thrown by the serialized when data can't be de-serialized which instructs DataStore how to replace the corrupted
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            // a list of dataMigration for moving prev. data into DataStore
            migrations = listOf(SharedPreferencesMigration(appContext, Constants.USER_PREFERENCES)),
            // The scope in which IO operations and transform functions will execute (using the same scope as the DataStore API default one)
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            // generates the file object for preferences DataStore based on the provided Context and name stored in "applicationContext.filesDir + datastore/${subdirectory}"
            produceFile = { appContext.preferencesDataStoreFile(Constants.USER_PREFERENCES) })
    }
}