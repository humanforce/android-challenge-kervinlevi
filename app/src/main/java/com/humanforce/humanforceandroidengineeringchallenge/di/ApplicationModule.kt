package com.humanforce.humanforceandroidengineeringchallenge.di

import APIKeyManager
import android.content.Context
import androidx.room.Room
import com.humanforce.humanforceandroidengineeringchallenge.BuildConfig
import com.humanforce.humanforceandroidengineeringchallenge.data.db.WeatherAppDb
import com.humanforce.humanforceandroidengineeringchallenge.data.location.LocationRepositoryImpl
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.WeatherApi
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.WeatherRepositoryImpl
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.weather.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by kervinlevi on 24/12/24
 */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideLocationProvider(
        @ApplicationContext context: Context, weatherApi: WeatherApi, weatherAppDb: WeatherAppDb
    ): LocationRepository {
        return LocationRepositoryImpl(
            context,
            weatherApi,
            weatherAppDb.favoriteLocationDao(),
            APIKeyManager.apiKey
        )
    }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder().baseUrl(BuildConfig.OPEN_WEATHER_URL)
            .addConverterFactory(MoshiConverterFactory.create()).build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApi: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, APIKeyManager.apiKey)
    }

    @Provides
    @Singleton
    fun provideWeatherAppDb(@ApplicationContext context: Context): WeatherAppDb {
        return Room.databaseBuilder(context, WeatherAppDb::class.java, "weather_app_db").build()
    }
}
