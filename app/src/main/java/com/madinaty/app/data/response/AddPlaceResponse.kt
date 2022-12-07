package com.madinaty.app.data.response

import com.squareup.moshi.JsonClass

//    {
//    "message": "Duplicate entry 'place_email2@mail.com' for key 'incoming_advertisement_requests_email_unique'",
//    "success": 0,
//    "query_error": 1
//}

//    {
//    "data": {
//        "status": true,
//        "message": "Your request is successfully sent",
//        "info": {
//            "target": "place name 3"
//        }
//    }
//}
@JsonClass(generateAdapter = true)
class AddPlaceResponse(
    val data: AddPlaceDataResponse?,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class AddPlaceDataResponse(
    val message: String
)

