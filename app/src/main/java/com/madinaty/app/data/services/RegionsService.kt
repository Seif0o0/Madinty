package com.madinaty.app.data.services

import com.madinaty.app.data.response.RegionsResponse
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RegionsService {
    @GET(Constants.ADMIN_ROOT.plus(Constants.REGIONS))
    suspend fun fetchRegions(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
    ): Response<RegionsResponse>
}