package com.humanforce.humanforceandroidengineeringchallenge.di

import APIKeyManager
import android.content.Context
import com.humanforce.humanforceandroidengineeringchallenge.BuildConfig
import com.humanforce.humanforceandroidengineeringchallenge.data.location.LocationProviderImpl
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.WeatherApi
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.WeatherRepositoryImpl
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationProvider
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by kervinlevi on 24/12/24
 */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider {
        return LocationProviderImpl(context)
    }

    @Provides
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPEN_WEATHER_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(WeatherApi::class.java)
    }

    @Provides
    fun provideWeatherRepository(weatherApi: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, APIKeyManager.apiKey)
    }
}
