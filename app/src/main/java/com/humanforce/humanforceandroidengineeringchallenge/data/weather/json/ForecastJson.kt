package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class ForecastJson(

    @Json(name = "dt_txt")
    val dateTime: String?,

    @Json(name = "main")
    val mainForecast: MainForecastJson?,

    @Json(name = "weather")
    val weatherList: List<WeatherDescriptionJson?>?,

    @Json(name = "rain")
    val rainForecast: RainForecastJson?,

    @Json(name = "pop")
    val precipitation: Float?
)
