package com.madinaty.app.data.paging

import android.app.Application
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toOffer
import com.madinaty.app.data.services.OffersService
import com.madinaty.app.domain.model.Department
import com.madinaty.app.domain.model.Offer
import com.madinaty.app.kot_pref.UserInfo
import java.io.IOException
import javax.inject.Inject

class OffersPagingSource @Inject constructor(
    private val application: Application,
    private val service: OffersService
) : PagingSource<Int, Offer>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Offer> {
        val currentPage = params.key ?: 1
        return try {
            val response =
                service.fetchOffers(token = "Bearer ${UserInfo.token}", page = currentPage)
            if (response.isSuccessful) {
                val body = response.body()!!
                val offers = body.data.map { it.toOffer() }

                LoadResult.Page(
                    data = offers,
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

    override fun getRefreshKey(state: PagingState<Int, Offer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}