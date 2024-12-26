package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent

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
    val oneTimeEvent: OneTimeEvent<out LocationEvent>? = null
)

sealed interface LocationError {
    object NoInternet: LocationError
    data class HttpError(val message: String): LocationError
    object EmptyQuery: LocationError
    object EmptyResult: LocationError
}

sealed interface LocationAction {
    data class UpdateQuery(val query: String): LocationAction
    object SearchLocation: LocationAction
    object UseUserLocation: LocationAction
    data class UpdateLocation(val location: Location): LocationAction
    data class SaveLocation(val location: Location): LocationAction
}

sealed interface LocationEvent {
    object SaveFavoriteSuccessful: LocationEvent
    object SaveFavoriteFailed: LocationEvent
}
