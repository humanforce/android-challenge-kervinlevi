package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.data.preference.WeatherAppPreference
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response.Error
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response.Success
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository

/**
 * Created by kervinlevi on 24/12/24
 */
class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val preference: WeatherAppPreference,
    private val appId: String
): WeatherRepository {

    override suspend fun getWeatherForecast(location: Location): Response<WeatherForecast> {
        try {
            val remoteData = weatherApi.getWeather(
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                units = preference.getTemperatureUnit().value,
                key = appId
            )
            val weatherReport = remoteData.toDomainModel()
            return Success(weatherReport)
        } catch (exception: Exception) {
            return Error(exception)
        }
    }

    override fun getRecentWeatherForecast(): WeatherForecast? {
        return preference.getRecentWeatherForecast()
    }

    override fun saveRecentWeatherForecast(weatherForecast: WeatherForecast?) {
        return preference.saveRecentWeatherForecast(weatherForecast)
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        return preference.getTemperatureUnit()
    }

    override fun updateTemperatureUnit(unit: TemperatureUnit) {
        preference.saveTemperatureUnit(unit)
    }
}
