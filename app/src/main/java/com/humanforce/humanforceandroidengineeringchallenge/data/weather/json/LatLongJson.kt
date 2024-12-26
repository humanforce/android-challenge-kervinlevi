package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class LatLongJson(
    @Json(name = "lat")
    val lat: Double?,

    @Json(name = "lon")
    val long: Double?
)
