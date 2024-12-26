package com.humanforce.humanforceandroidengineeringchallenge.domain.model

import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class WeatherUpdate(
    val date: String?,
    val time: String?,
    val temperature: Float?,
    val feelsLikeTemp: Float?,
    val humidity: Int?,
    val condition: WeatherCondition?,
    val conditionDescription: String?,
    val chanceOfRain: Int?
)
