package com.humanforce.humanforceandroidengineeringchallenge.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationProvider
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by kervinlevi on 24/12/24
 */
class LocationProviderImpl @Inject constructor(private val context: Context) : LocationProvider {
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        if (!isLocationPermissionGranted() || !isLocationEnabled()) {
            return null
        }

        return suspendCoroutine { coroutine ->
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val request =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val fuseLocation = p0.lastLocation
                    if (fuseLocation == null) {
                        coroutine.resume(null)
                        removeCallback()
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(fuseLocation.latitude,
                            fuseLocation.longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    coroutine.resume(
                                        getLocation(
                                            fuseLocation, addresses.firstOrNull()
                                        )
                                    )
                                    removeCallback()
                                }

                                override fun onError(errorMessage: String?) {
                                    coroutine.resume(getLocation(fuseLocation, null))
                                    removeCallback()
                                }
                            })
                    } else {
                        val addresses = geocoder.getFromLocation(
                            fuseLocation.latitude, fuseLocation.longitude, 1
                        )
                        coroutine.resume(getLocation(fuseLocation, addresses?.firstOrNull()))
                        removeCallback()
                    }
                }

                private fun removeCallback() {
                    locationProviderClient.removeLocationUpdates(this)
                }
            }

            with(locationProviderClient.requestLocationUpdates(request, locationCallback, null)) {
                addOnFailureListener {
                    coroutine.resume(null)
                    locationProviderClient.removeLocationUpdates(locationCallback)
                }
                addOnCanceledListener {
                    coroutine.resume(null)
                    locationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    override fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLocation(
        androidLocation: android.location.Location, address: Address?
    ): Location {
        return Location(
            latitude = androidLocation.latitude,
            longitude = androidLocation.longitude,
            city = address?.locality,
            country = address?.countryName
        )
    }
}
