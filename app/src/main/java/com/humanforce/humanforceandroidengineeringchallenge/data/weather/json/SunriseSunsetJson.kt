package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 27/12/24
 */

@JsonClass(generateAdapter = true)
data class SunriseSunsetJson(
    @Json(name = "sunrise")
    val sunrise: Long?,

    @Json(name = "sunset")
    val sunset: Long?,
)
