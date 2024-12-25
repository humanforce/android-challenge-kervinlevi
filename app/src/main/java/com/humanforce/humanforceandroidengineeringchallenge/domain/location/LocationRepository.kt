package com.humanforce.humanforceandroidengineeringchallenge.domain.location

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by kervinlevi on 24/12/24
 */
interface LocationRepository {
    val selectedLocation: StateFlow<Location>

    suspend fun getUserLocation(): Location?

    fun isLocationPermissionGranted(): Boolean

    fun updateLocation(location: Location)

    suspend fun searchLocations(query: String): Response<List<Location>>
}