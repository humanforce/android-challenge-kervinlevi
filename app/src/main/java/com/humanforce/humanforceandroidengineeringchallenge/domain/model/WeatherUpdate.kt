package com.humanforce.humanforceandroidengineeringchallenge.domain.model

/**
 * Created by kervinlevi on 24/12/24
 */
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
