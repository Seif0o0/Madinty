package com.madinaty.app.data.services

import com.madinaty.app.data.response.PhoneLoginResponse
import com.madinaty.app.data.response.ProfileResponse
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {
    @POST(Constants.AUTH_ROOT.plus(Constants.LOGIN))
    suspend fun login(
        @Query("mobile") phoneNumber: String,
        @Query("password") password: String
    ): Response<PhoneLoginResponse>

    @GET(Constants.AUTH_ROOT.plus(Constants.PROFILE))
    suspend fun fetchProfileInfo(@Header("Authorization") token: String): Response<ProfileResponse>
}