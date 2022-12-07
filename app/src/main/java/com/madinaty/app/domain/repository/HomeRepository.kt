package com.madinaty.app.domain.repository

import androidx.paging.PagingData
import com.madinaty.app.data.response.DepartmentsResponse
import com.madinaty.app.domain.model.Department
import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun fetchDepartments(): Flow<PagingData<Department>>
}