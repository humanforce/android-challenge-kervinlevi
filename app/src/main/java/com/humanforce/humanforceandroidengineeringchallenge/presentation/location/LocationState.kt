package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location

/**
 * Created by kervinlevi on 25/12/24
 */
data class LocationState(
    val query: String = "",
    val searchResults: List<Location> = emptyList(),
    val isSearchLoading: Boolean = false,
    val searchError: LocationError? = null,
    val requestLocationPermissions: Boolean = false,
    val showLocationPermissionRationale: Boolean = false,
)

sealed interface LocationError {
    object NoInternet: LocationError
    data class HttpError(val message: String): LocationError
    object LocationUnavailable: LocationError
    object EmptyQuery: LocationError
    object EmptyResult: LocationError
}

sealed interface LocationAction {
    object ShowPermissionsRationale: LocationAction
    object PermissionGranted: LocationAction

    data class UpdateQuery(val query: String): LocationAction
    object SearchLocation: LocationAction
    object UseUserLocation: LocationAction

    data class UpdateLocation(val location: Location): LocationAction
}
