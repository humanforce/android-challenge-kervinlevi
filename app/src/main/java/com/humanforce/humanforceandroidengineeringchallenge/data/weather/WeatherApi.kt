package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.SearchLocationResponseJson
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.WeatherResponseJson
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kervinlevi on 24/12/24
 */
interface WeatherApi {

    @GET("data/2.5/forecast")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") key: String,
        @Query("units") units: String = "metric"
    ): WeatherResponseJson

    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("appid") key: String,
    ): List<SearchLocationResponseJson>
}
