package com.humanforce.humanforceandroidengineeringchallenge.domain.model

import com.squareup.moshi.JsonClass

/**
 * Created by kervinlevi on 24/12/24
 */
@JsonClass(generateAdapter = true)
data class Location(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val userLocation: Boolean
)
