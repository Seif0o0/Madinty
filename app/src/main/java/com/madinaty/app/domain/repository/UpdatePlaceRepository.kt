package com.madinaty.app.domain.repository

import com.madinaty.app.utils.DataState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UpdatePlaceRepository {
    fun editPlace(
        placeId: String,
        map: Map<String, RequestBody>,
        images: MutableList<MultipartBody.Part>?
    ): Flow<DataState<String>>
}