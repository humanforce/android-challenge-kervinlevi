package com.humanforce.humanforceandroidengineeringchallenge.data.preference

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast

/**
 * Created by kervinlevi on 26/12/24
 */
interface WeatherAppPreference {

    fun getRecentLocation(): Location?
    fun saveRecentLocation(location: Location?)
    fun saveRecentWeatherForecast(weatherForecast: WeatherForecast?)
    fun getRecentWeatherForecast(): WeatherForecast?
}
