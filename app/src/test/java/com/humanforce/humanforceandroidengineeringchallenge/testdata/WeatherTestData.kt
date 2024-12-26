package com.humanforce.humanforceandroidengineeringchallenge.testdata

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherCondition
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherUpdate

/**
 * Created by kervinlevi on 27/12/24
 */
val updates1 = listOf(
    listOf(
        WeatherUpdate(
            date = "December 27",
            time = "9PM",
            temperature = 26.5f,
            feelsLikeTemp = 27.0f,
            humidity = 88,
            condition = WeatherCondition.NIGHT_CLOUDY,
            chanceOfRain = 11,
            conditionDescription = "few clouds"

        )
    )
)

val forecast1 = WeatherForecast(
    location = favoriteLocation1,
    updates = updates1,
    sunrise = "6:00 am",
    sunset = "5:30 pm",
    retrievedOn = "Dec 27, 2024 9:30 pm",
    gmtTimezone = 8
)
