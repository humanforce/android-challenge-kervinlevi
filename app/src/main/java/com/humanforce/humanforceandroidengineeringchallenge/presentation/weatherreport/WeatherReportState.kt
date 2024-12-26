package com.humanforce.humanforceandroidengineeringchallenge.presentation.weatherreport

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent

/**
 * Created by kervinlevi on 24/12/24
 */
data class WeatherReportState(
    val isLoading: Boolean = false,
    val requestLocationPermissions: Boolean = false,
    val showLocationPermissionRationale: Boolean = false,
    val location: Location? = null,
    val weatherForecast: WeatherForecast? = null,
    val oneTimeEvent: OneTimeEvent<out WeatherReportError>? = null,
    val onScreenError: WeatherReportError? = null,
    val activeTemperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)

sealed interface WeatherReportError {
    object NoInternet: WeatherReportError
    data class HttpError(val message: String): WeatherReportError
    object LocationUnavailable: WeatherReportError
}

sealed interface WeatherReportAction {
    object ShowPermissionsRationale: WeatherReportAction
    object PermissionGranted: WeatherReportAction
    object OnPullToRefresh: WeatherReportAction
    object OnLocationUpdated: WeatherReportAction

    data class UpdateTemperatureUnit(val temperatureUnit: TemperatureUnit): WeatherReportAction
}
