package com.madinaty.app.data.services

import com.madinaty.app.data.response.AddPlaceResponse
import com.madinaty.app.data.response.PlacesResponse
import com.madinaty.app.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PlacesService {
    @GET(Constants.ADMIN_ROOT.plus(Constants.PLACES))
    suspend fun fetchPlaces(
        @Header("Authorization") token: String,
        @QueryMap map: Map<String, String>,
    ): Response<PlacesResponse>

    @Multipart
    @POST(Constants.ADMIN_ROOT.plus(Constants.ADD_PLACE))
    suspend fun createPlace(
        @Header("Authorization") token: String,
        @PartMap map: MutableMap<String, RequestBody>,
        @Part files: MutableList<MultipartBody.Part>
    ): Response<AddPlaceResponse>


    @Multipart
    @POST("${Constants.ADMIN_ROOT}${Constants.ADD_PLACE}/{id}")
    suspend fun updatePlace(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("_method") method: String = "PUT",
        @PartMap map: MutableMap<String, RequestBody>,
        @Part files: MutableList<MultipartBody.Part>?
    ): Response<AddPlaceResponse>

}