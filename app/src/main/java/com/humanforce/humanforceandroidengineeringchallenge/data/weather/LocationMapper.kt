package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.SearchLocationResponseJson
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import java.util.Locale

/**
 * Created by kervinlevi on 25/12/24
 */
fun List<SearchLocationResponseJson>.toDomainModel(): List<Location> {
    return this.map {
        var cityName = it.name
        it.state?.let {  state ->
            cityName += ", $state"
        }
        Location(
            city = cityName,
            country = (it.countryCode)?.let { countryCode ->
                Locale(
                    "",
                    countryCode
                ).displayCountry
            } ?: it.countryCode,
            latitude = it.lat,
            longitude = it.long,
            userLocation = false
        )
    }
}
