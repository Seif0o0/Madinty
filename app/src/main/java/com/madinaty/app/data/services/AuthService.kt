package com.madinaty.app.data.services

import com.madinaty.app.data.model.PlaceDto
import com.madinaty.app.data.response.*
import com.madinaty.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface AuthService {
    @POST("${Constants.AUTH_ROOT}${Constants.LOGIN}")
    suspend fun login(
        @Query("mobile") phoneNumber: String,
        @Query("password") password: String
    ): Response<PhoneLoginResponse>

    @GET("${Constants.AUTH_ROOT}${Constants.PROFILE}")
    suspend fun fetchProfileInfo(@Header("Authorization") token: String): Response<ProfileResponse>

    @PUT("${Constants.AUTH_ROOT}${Constants.EDIT_PROFILE}")
    suspend fun editProfileInfo(
        @Header("Authorization") token: String,
        @QueryMap map: Map<String, String>
    ): Response<EditProfileResponse>

    @POST("${Constants.AUTH_ROOT}${Constants.FAVOURITES}")
    suspend fun addRemoveFavourite(
        @Header("Authorization") token: String,
        @Query("incoming_advertisement_request_id") placeId: String
    ): Response<AddRemoveFavouriteResponse>

    @GET("${Constants.AUTH_ROOT}${Constants.FAVOURITES}")
    suspend fun fetchFavourites(
        @Header("Authorization") token: String,
    ): Response<List<FavouritesResponse>>

    @GET("${Constants.AUTH_ROOT}${Constants.MY_PLACES}")
    suspend fun fetchMyPlaces(
        @Header("Authorization") token: String
    ): Response<List<PlaceDto>>

}