package com.humanforce.humanforceandroidengineeringchallenge.data.weather.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 25/12/24
 */
@JsonClass(generateAdapter = true)
data class SearchLocationResponseJson(
    @Json(name = "name")
    val name: String?,

    @Json(name = "lat")
    val lat: Double?,

    @Json(name = "lon")
    val long: Double?,

    @Json(name = "country")
    val countryCode: String?,

    @Json(name = "state")
    val state: String?
)
