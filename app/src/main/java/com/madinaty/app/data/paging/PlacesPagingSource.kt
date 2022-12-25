package com.madinaty.app.data.paging

import android.app.Application
import android.util.Log
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
    private val departmentId: String?,
    private val query: String?
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
            val map = mutableMapOf<String, String>()

            map["page"] = currentPage.toString()
            departmentId?.let { map["department_id"] = it }
            query?.let { map["search"] = it }

            val response =
                service.fetchPlaces(
                    token = "Bearer ${UserInfo.token}",
                    map = map
                )
            if (response.isSuccessful) {
                val body = response.body()!!
                val meta = body.meta
                val places = body.data.map { it.toPlace() }

                LoadResult.Page(
                    data = places,
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
            LoadResult.Error(Exception(application.getString(R.string.something_went_wrong_try_again_later)))
        }
    }
}










