package com.madinaty.app.data.services

import com.madinaty.app.data.response.DepartmentsResponse
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DepartmentsService {
    @GET(Constants.ADMIN_ROOT.plus(Constants.DEPARTMENTS))
    suspend fun fetchDepartments(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Response<DepartmentsResponse>
}