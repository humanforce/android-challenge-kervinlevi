package com.humanforce.humanforceandroidengineeringchallenge.data.preference

import android.content.Context
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit.CELSIUS
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit.valueOf
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.squareup.moshi.Moshi

/**
 * Created by kervinlevi on 26/12/24
 */
class WeatherAppPreferenceImpl(context: Context): WeatherAppPreference {

    private val sharedPreference = context.getSharedPreferences(
        "weather_app_prefs",
        Context.MODE_PRIVATE
    )

    private val moshi = Moshi.Builder().build()

    override fun getRecentLocation(): Location? {
        val locationJson = sharedPreference.getString(KEY_RECENT_LOCATION, null) ?: return null
        return moshi.adapter(Location::class.java).fromJson(locationJson)
    }

    override fun saveRecentLocation(location: Location?) {
        val locationJson = moshi.adapter(Location::class.java).toJson(location)
        sharedPreference.edit().putString(KEY_RECENT_LOCATION, locationJson).apply()
    }

    override fun getRecentWeatherForecast(): WeatherForecast? {
        val weatherJson = sharedPreference.getString(KEY_RECENT_FORECAST, null) ?: return null
        return moshi.adapter(WeatherForecast::class.java).fromJson(weatherJson)
    }

    override fun getTemperatureUnit(): TemperatureUnit {
        val unit = sharedPreference.getInt(KEY_TEMPERATURE_UNIT, 0)
        return TemperatureUnit.entries.getOrNull(unit) ?: CELSIUS
    }

    override fun saveTemperatureUnit(unit: TemperatureUnit) {
        sharedPreference.edit().putInt(KEY_TEMPERATURE_UNIT, unit.ordinal).apply()
    }

    override fun saveRecentWeatherForecast(weatherForecast: WeatherForecast?) {
        val weatherJson = moshi.adapter(WeatherForecast::class.java).toJson(weatherForecast)
        sharedPreference.edit().putString(KEY_RECENT_FORECAST, weatherJson).apply()
    }
    companion object {
        private const val KEY_RECENT_LOCATION = "key_recent_location"
        private const val KEY_RECENT_FORECAST = "key_recent_forecast"
        private const val KEY_TEMPERATURE_UNIT = "key_temperature_unit"
    }
}
