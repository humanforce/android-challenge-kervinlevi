package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent
import com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport.WeatherReportError.LocationUnavailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by kervinlevi on 24/12/24
 */
@HiltViewModel
class WeatherReportViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val selectedLocation = locationRepository.selectedLocation
    var state = mutableStateOf(
        WeatherReportState(
            location = selectedLocation.value,
            weatherForecast = weatherRepository.getRecentWeatherForecast(),
            activeTemperatureUnit = weatherRepository.getTemperatureUnit()
        )
    )
        private set

    init {
        val requestUserLocation =
            !locationRepository.isLocationPermissionGranted() && selectedLocation.value.userLocation
        state.value = state.value.copy(
            requestLocationPermissions = requestUserLocation
        )
        if (!requestUserLocation) {
            requestLocationAndWeather()
        }
    }

    private fun requestLocationAndWeather() = viewModelScope.launch {
        if (state.value.isLoading || state.value.requestLocationPermissions) return@launch
        state.value = state.value.copy(isLoading = true, oneTimeEvent = null, onScreenError = null)

        val location = if (selectedLocation.value.userLocation) {
            (locationRepository.getUserLocation() as? Response.Success)?.data
        } else {
            selectedLocation.value
        }

        if (location == null) {
            val hasCache = state.value.weatherForecast != null
            state.value = state.value.copy(
                isLoading = false,
                oneTimeEvent = if (hasCache) OneTimeEvent(LocationUnavailable) else null,
                onScreenError = if (hasCache) null else LocationUnavailable
            )
            return@launch
        }

        when (val apiResponse = weatherRepository.getWeatherForecast(location)) {
            is Response.Success -> {
                state.value = state.value.copy(
                    isLoading = false,
                    location = location,
                    weatherForecast = apiResponse.data,
                    activeTemperatureUnit = weatherRepository.getTemperatureUnit(),
                    oneTimeEvent = null,
                    onScreenError = null
                )
                locationRepository.saveRecentLocation(location)
                weatherRepository.saveRecentWeatherForecast(apiResponse.data)
            }

            is Response.Error -> {
                val exception = apiResponse.exception
                val hasCache = state.value.weatherForecast != null
                val error = if (exception is HttpException) {
                    WeatherReportError.HttpError("${exception.code()}: ${exception.message()}")
                } else {
                    WeatherReportError.NoInternet
                }
                state.value = state.value.copy(
                    isLoading = false,
                    location = location,
                    oneTimeEvent = if (hasCache) OneTimeEvent(error) else null,
                    onScreenError = if (hasCache) null else error
                )
            }
        }
    }

    fun onAction(action: WeatherReportAction) {
        when (action) {
            WeatherReportAction.PermissionGranted -> {
                state.value = state.value.copy(
                    requestLocationPermissions = false, showLocationPermissionRationale = false
                )
                requestLocationAndWeather()
            }

            WeatherReportAction.PermissionDenied -> {
                val hasCache = state.value.weatherForecast != null
                state.value = state.value.copy(
                    requestLocationPermissions = false,
                    showLocationPermissionRationale = true,
                    oneTimeEvent = if (hasCache) OneTimeEvent(LocationUnavailable) else null,
                    onScreenError = if (hasCache) null else LocationUnavailable
                )
            }

            WeatherReportAction.OnPullToRefresh -> {
                requestLocationAndWeather()
            }

            WeatherReportAction.OnLocationUpdated -> {
                requestLocationAndWeather()
            }

            is WeatherReportAction.UpdateTemperatureUnit -> {
                weatherRepository.updateTemperatureUnit(action.temperatureUnit)
                requestLocationAndWeather()
            }
        }
    }
}
