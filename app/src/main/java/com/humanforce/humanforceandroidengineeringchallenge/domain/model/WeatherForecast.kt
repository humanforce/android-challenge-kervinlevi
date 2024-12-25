package com.humanforce.humanforceandroidengineeringchallenge.domain.model

/**
 * Created by kervinlevi on 24/12/24
 */
data class WeatherForecast(
    val location: Location?,
    val updates: List<List<WeatherUpdate>>?,
    val sunrise: String?,
    val sunset: String?,
    val retrievedOn: String,
    val gmtTimezone: Int?
)
