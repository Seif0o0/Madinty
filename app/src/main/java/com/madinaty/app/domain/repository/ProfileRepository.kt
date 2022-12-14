package com.madinaty.app.domain.repository

import com.madinaty.app.domain.model.User
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow


interface ProfileRepository {
    fun fetchProfileInfo(): Flow<DataState<User>>
    fun updateProfileInfo(map: Map<String,String>):Flow<DataState<User>>
}