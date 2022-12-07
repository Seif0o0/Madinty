package com.madinaty.app.data.services

import com.madinaty.app.data.response.CommonResponse
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface CommonInfoService {
    @GET(Constants.COMMON_INFO)
    suspend fun fetchCommonInfo(): Response<CommonResponse>
}