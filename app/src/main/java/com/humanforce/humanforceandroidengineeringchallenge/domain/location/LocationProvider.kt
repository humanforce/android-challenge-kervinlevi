package com.humanforce.humanforceandroidengineeringchallenge.domain.location

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location

/**
 * Created by kervinlevi on 24/12/24
 */
interface LocationProvider {

    suspend fun getCurrentLocation(): Location?

    fun isLocationPermissionGranted(): Boolean
}
