package com.humanforce.humanforceandroidengineeringchallenge.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.humanforce.humanforceandroidengineeringchallenge.data.db.FavoriteLocationDao
import com.humanforce.humanforceandroidengineeringchallenge.data.preference.WeatherAppPreference
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.WeatherApi
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationUnavailableException
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by kervinlevi on 24/12/24
 */
class LocationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val weatherApi: WeatherApi,
    private val favoriteLocationDao: FavoriteLocationDao,
    private val preference: WeatherAppPreference,
    private val appId: String
) : LocationRepository {

    private val _selectedLocation =
        MutableStateFlow(preference.getRecentLocation() ?: Location(userLocation = true))
    override val selectedLocation: StateFlow<Location> = _selectedLocation

    override val favoriteLocations: Flow<List<Location>> =
        favoriteLocationDao.getFavoriteLocations().map { it.toDomainModels() }

    override suspend fun getUserLocation(): Response<Location> {
        if (!isLocationPermissionGranted() || !isLocationEnabled()) {
            return Response.Error(LocationUnavailableException("Check permission and settings."))
        }
        getUserCoordinates()?.let {
            val response = getLocationByCoordinates(it.first, it.second)
            return response
        }
        return Response.Error(LocationUnavailableException("Check permission and settings."))
    }

    @SuppressLint("MissingPermission")
    private suspend fun getUserCoordinates(): Pair<Double, Double>? {
        return suspendCoroutine { coroutine ->
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val request =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    val fuseLocation = p0.lastLocation
                    if (fuseLocation == null) {
                        coroutine.resume(null)
                        removeCallback()
                    } else {
                        coroutine.resume(Pair(fuseLocation.longitude, fuseLocation.latitude))
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

    override fun updateLocation(location: Location) {
        _selectedLocation.value = location
    }

    override suspend fun searchLocations(query: String): Response<List<Location>> {
        try {
            val remoteData = weatherApi.searchLocation(query = query, limit = 10, key = appId)
            val searchResult = remoteData.toDomainModel()
            return Response.Success(searchResult)
        } catch (exception: Exception) {
            return Response.Error(exception)
        }
    }

    override suspend fun getLocationByCoordinates(
        longitude: Double,
        latitude: Double
    ): Response<Location> {
        try {
            val remoteData = weatherApi.getLocationByCoordinates(longitude, latitude, 1, appId)
            return remoteData.firstOrNull()?.let {
                Response.Success(
                    it.toDomainModel()
                        .copy(latitude = latitude, longitude = longitude, userLocation = true)
                )
            } ?: Response.Error(Exception("Empty result"))
        } catch (exception: Exception) {
            return Response.Error(exception)
        }
    }

    override suspend fun addFavoriteLocation(location: Location): Response<Long> {
        val rowId = favoriteLocationDao.insert(location.toDbEntity())
        return if (rowId > 0) {
            Response.Success(rowId)
        } else {
            Response.Error(Exception("Failed to insert data."))
        }
    }

    override fun getRecentLocation(): Location? {
        return preference.getRecentLocation()
    }

    override fun saveRecentLocation(location: Location?) {
        return preference.saveRecentLocation(location)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
