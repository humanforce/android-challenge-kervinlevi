package com.humanforce.humanforceandroidengineeringchallenge.domain.weather

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast

/**
 * Created by kervinlevi on 24/12/24
 */
interface WeatherRepository {

    suspend fun getWeatherForecast(location: Location): Response<WeatherForecast>

    fun getRecentWeatherForecast(): WeatherForecast?
    fun saveRecentWeatherForecast(weatherForecast: WeatherForecast?)
    fun updateTemperatureUnit(unit: TemperatureUnit)
    fun getTemperatureUnit(): TemperatureUnit
}
