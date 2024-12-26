package com.humanforce.humanforceandroidengineeringchallenge.data.location

import com.humanforce.humanforceandroidengineeringchallenge.data.db.FavoriteLocationEntity
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.SearchLocationResponseJson
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import java.util.Locale

/**
 * Created by kervinlevi on 25/12/24
 */
fun SearchLocationResponseJson.toDomainModel(): Location {
    return Location(
        city = name,
        state = state,
        country = (this.countryCode)?.let { countryCode ->
            Locale(
                "", countryCode
            ).displayCountry
        } ?: this.countryCode,
        latitude = this.lat,
        longitude = this.long,
        userLocation = false)
}

fun List<SearchLocationResponseJson>.toDomainModel(): List<Location> {
    return this.map { it.toDomainModel() }
}

fun FavoriteLocationEntity.toDomainModel(): Location {
    return Location(
        latitude = latitude,
        longitude = longitude,
        city = city,
        state = state,
        country = country,
        userLocation = false
    )
}

fun List<FavoriteLocationEntity>.toDomainModels(): List<Location> {
    return this.map { it.toDomainModel() }
}

fun Location.toDbEntity(): FavoriteLocationEntity {
    return FavoriteLocationEntity(
        latitude = latitude,
        longitude = longitude,
        city = city,
        state = state,
        country = country
    )
}
