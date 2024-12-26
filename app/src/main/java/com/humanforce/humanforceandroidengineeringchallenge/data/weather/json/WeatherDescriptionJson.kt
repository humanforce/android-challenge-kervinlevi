package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class WeatherDescriptionJson(

    @Json(name = "id")
    val weatherId: Int?,

    @Json(name = "main")
    val shortDescription: String?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "icon")
    val iconId: String?,
)
