package com.humanforce.humanforceandroidengineeringchallenge.domain.model

import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class WeatherForecast(
    val location: Location?,
    val current: WeatherUpdate?,
    val updates: List<List<WeatherUpdate>>?,
    val sunrise: String?,
    val sunset: String?,
    val retrievedOn: String,
    val gmtTimezone: Int?
)
