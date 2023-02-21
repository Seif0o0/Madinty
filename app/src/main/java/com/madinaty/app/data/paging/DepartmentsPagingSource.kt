package com.madinaty.app.data.paging

import android.app.Application
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toDepartment
import com.madinaty.app.data.services.DepartmentsService
import com.madinaty.app.domain.model.Department
import com.madinaty.app.kot_pref.UserInfo
import java.io.IOException
import javax.inject.Inject


class DepartmentsPagingSource @Inject constructor(
    private val application: Application,
    private val service: DepartmentsService
) : PagingSource<Int, Department>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Department> {

        val currentPage = params.key ?: 1
        return try {
            val response =
                service.fetchDepartments(
                    token = if (UserInfo.userId.isEmpty()) "" else "Bearer ${UserInfo.token}",
                    page = currentPage
                )
            if (response.isSuccessful) {
                val body = response.body()!!
                val meta = body.meta
                val departments = body.data.map { it.toDepartment() }

                LoadResult.Page(
                    data = departments,
                    prevKey = if (meta.currentPage == 1) null else meta.currentPage - 1,
                    nextKey = if (meta.next == null || meta.next == meta.totalPages) null else meta.next
                )
            } else {
                when (response.code()) {
                    500 ->
                        LoadResult.Error(Exception(application.getString(R.string.server_error_message)))
                    else ->
                        LoadResult.Error(Exception(application.getString(R.string.something_went_wrong_try_again_later)))
                }
            }
        } catch (e: IOException) {
            LoadResult.Error(Exception(application.getString(R.string.no_internet_connection)))
        } catch (e: Exception) {
            Log.d("DepartmentsPagingSource", "error: ${e.message}")
            LoadResult.Error(Exception(e.message ?: ""))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Department>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}