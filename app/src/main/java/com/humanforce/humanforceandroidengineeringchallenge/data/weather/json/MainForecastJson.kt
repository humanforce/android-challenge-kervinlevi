package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class MainForecastJson(

    @Json(name = "temp")
    val temperature: Float?,

    @Json(name = "feels_like")
    val temperatureFeelsLike: Float?,

    @Json(name = "humidity")
    val humidity: Int?,
)
