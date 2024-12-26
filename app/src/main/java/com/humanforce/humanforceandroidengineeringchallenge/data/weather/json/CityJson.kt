package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class CityJson(

    @Json(name = "name")
    val name: String?,

    @Json(name = "coord")
    val coordinates: LatLongJson?,

    @Json(name = "country")
    val country: String?,

    @Json(name = "timezone")
    val timezone: Long?,

    @Json(name = "sunrise")
    val sunrise: Long?,

    @Json(name = "sunset")
    val sunset: Long?,
)
