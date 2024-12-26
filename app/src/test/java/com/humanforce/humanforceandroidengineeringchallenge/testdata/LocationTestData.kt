package com.humanforce.humanforceandroidengineeringchallenge.testdata

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException

/**
 * Created by kervinlevi on 27/12/24
 */
val favoriteLocation1 = Location(
    city = "Taguig",
    latitude = 14.5270538,
    longitude = 121.0744942,
    state = "Metro Manila",
    country = "Philippines",
    userLocation = false
)

val searchResponseSuccess = Response.Success(listOf(favoriteLocation1))
val searchResponseEmpty = Response.Success(emptyList<Location>())
val searchResponseError = Response.Error<List<Location>>(Exception("Error"))
val searchResponseHttpError = Response.Error<List<Location>>(
    HttpException(
        retrofit2.Response.error<List<Location>>(
            400, ResponseBody.create(MediaType.parse("application/json"), "Unauthorized")
        )
    )
)
