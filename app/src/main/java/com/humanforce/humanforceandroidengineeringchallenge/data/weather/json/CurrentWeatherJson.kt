package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 27/12/24
 */
@JsonClass(generateAdapter = true)
data class CurrentWeatherJson(
    @Json(name = "main")
    val mainForecast: MainForecastJson?,

    @Json(name = "sys")
    val sunriseSunset: SunriseSunsetJson?,

    @Json(name = "weather")
    val weather: List<WeatherDescriptionJson?>?,

    @Json(name = "dt")
    val dateTime: String?
)
