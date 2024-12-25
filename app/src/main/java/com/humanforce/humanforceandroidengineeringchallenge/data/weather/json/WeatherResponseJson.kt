package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class WeatherResponseJson(
    @Json(name = "list") val forecasts: List<ForecastJson?>?,

    @Json(name = "city") val city: CityJson?,
)
