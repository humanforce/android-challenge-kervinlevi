package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationProvider
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository
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
    private val locationProvider: LocationProvider,
    private val weatherRepository: WeatherRepository
): ViewModel() {

    var weatherReportState = mutableStateOf(WeatherReportState())
        private set

    init {
        val locationPermissionGranted = locationProvider.isLocationPermissionGranted()
        weatherReportState.value = weatherReportState.value.copy(
            requestLocationPermissions = !locationPermissionGranted,
            error = if (!locationPermissionGranted) LocationUnavailable else null
        )
        if (locationProvider.isLocationPermissionGranted()) {
            requestLocationAndWeather()
        }
    }

    private fun requestLocationAndWeather() = viewModelScope.launch {
        if (weatherReportState.value.isLoading) return@launch
        weatherReportState.value = weatherReportState.value.copy(isLoading = true)

        val location = locationProvider.getCurrentLocation()
        if (location != null) {
            when (val apiResponse = weatherRepository.getWeatherForecast(location)) {
                is Response.Success -> {
                    weatherReportState.value = weatherReportState.value.copy(
                        isLoading = false,
                        location = apiResponse.data.location,
                        weatherForecast = apiResponse.data,
                        error = null
                    )
                }

                is Response.Error -> {
                    val exception = apiResponse.exception
                    val error = if (exception is HttpException) {
                        WeatherReportError.HttpError("${exception.code()}: ${exception.message()}")
                    } else {
                        WeatherReportError.NoInternet
                    }
                    weatherReportState.value = weatherReportState.value.copy(
                        isLoading = false,
                        location = location,
                        weatherForecast = null,
                        error = error
                    )
                }
            }
        } else {
            weatherReportState.value = weatherReportState.value.copy(
                isLoading = false,
                location = null,
                weatherForecast = null,
                error = LocationUnavailable
            )
        }
    }

    fun onAction(action: WeatherReportAction) {
        when (action) {
            WeatherReportAction.PermissionGranted -> {
                weatherReportState.value = weatherReportState.value.copy(
                    requestLocationPermissions = false,
                    showLocationPermissionRationale = false
                )
                requestLocationAndWeather()
            }

            WeatherReportAction.ShowPermissionsRationale -> {
                weatherReportState.value = weatherReportState.value.copy(
                    requestLocationPermissions = false,
                    showLocationPermissionRationale = true
                )
            }

            WeatherReportAction.OnPullToRefresh -> {
                requestLocationAndWeather()
            }
        }
    }
}
