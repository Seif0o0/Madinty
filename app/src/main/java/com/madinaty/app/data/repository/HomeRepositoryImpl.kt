package com.madinaty.app.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.madinaty.app.data.paging.DepartmentsPagingSource
import com.madinaty.app.data.services.DepartmentsService
import com.madinaty.app.domain.model.Department
import com.madinaty.app.domain.repository.HomeRepository
import com.madinaty.app.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val application: Application,
    private val service: DepartmentsService
) : HomeRepository {

    override fun fetchDepartments(): Flow<PagingData<Department>> {
        return Pager(PagingConfig(pageSize = Constants.PAGE_SIZE)) {
            DepartmentsPagingSource(application, service)
        }.flow
    }

}