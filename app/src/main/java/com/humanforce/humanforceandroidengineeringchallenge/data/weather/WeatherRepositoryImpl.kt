package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response.Error
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response.Success
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository

/**
 * Created by kervinlevi on 24/12/24
 */
class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi,
    private val appId: String
): WeatherRepository {

    override suspend fun getWeatherForecast(location: Location): Response<WeatherForecast> {
        try {
            val remoteData = weatherApi.getWeather(
                latitude = location.latitude.toString(),
                longitude = location.longitude.toString(),
                key = appId
            )
            val weatherReport = remoteData.toDomainModel()
            return Success(weatherReport)
        } catch (exception: Exception) {
            return Error(exception)
        }
    }
}
