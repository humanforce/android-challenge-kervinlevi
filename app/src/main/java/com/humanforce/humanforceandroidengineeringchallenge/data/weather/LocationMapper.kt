package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.SearchLocationResponseJson
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import java.util.Locale

/**
 * Created by kervinlevi on 25/12/24
 */
fun SearchLocationResponseJson.toDomainModel(): Location {
    var cityName = this.name
    this.state?.let { state ->
        cityName += ", $state"
    }
    return Location(city = cityName, country = (this.countryCode)?.let { countryCode ->
        Locale(
            "", countryCode
        ).displayCountry
    } ?: this.countryCode, latitude = this.lat, longitude = this.long, userLocation = false)
}

fun List<SearchLocationResponseJson>.toDomainModel(): List<Location> {
    return this.map { it.toDomainModel() }
}
