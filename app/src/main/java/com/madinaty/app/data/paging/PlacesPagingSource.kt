package com.madinaty.app.data.paging

import android.app.Application
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toPlace
import com.madinaty.app.data.services.PlacesService
import com.madinaty.app.domain.model.Place
import com.madinaty.app.kot_pref.UserInfo
import java.io.IOException
import javax.inject.Inject

class PlacesPagingSource @Inject constructor(
    private val application: Application,
    private val service: PlacesService,
    private val departmentId: String,
) : PagingSource<Int, Place>() {

    override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
        val currentPage = params.key ?: 1
        return try {
            val response =
                service.fetchPlaces(
                    token = "Bearer ${UserInfo.token}",
                    page = currentPage,
                    id = departmentId
                )
            if (response.isSuccessful) {
                val body = response.body()!!
                val places = body.data.map { it.toPlace() }

                LoadResult.Page(
                    data = places,
                    prevKey = if (body.prev == null) null else body.currentPage - 1,
                    nextKey = if (body.currentPage == body.totalPages) null else body.currentPage + 1
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
        }
    }
}










