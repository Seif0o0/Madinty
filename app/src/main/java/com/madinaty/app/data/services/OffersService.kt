package com.madinaty.app.data.services

import com.madinaty.app.data.response.OffersResponse
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OffersService {
    @GET(Constants.ADMIN_ROOT.plus(Constants.OFFERS))
    suspend fun fetchOffers(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Response<OffersResponse>
}