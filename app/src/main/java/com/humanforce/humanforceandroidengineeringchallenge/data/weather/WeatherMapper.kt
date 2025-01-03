package com.humanforce.humanforceandroidengineeringchallenge.data.weather

import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.CurrentWeatherJson
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.WeatherDescriptionJson
import com.humanforce.humanforceandroidengineeringchallenge.data.weather.json.WeatherForecastJson
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherCondition
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherForecast
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherUpdate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 * Created by kervinlevi on 24/12/24
 */

fun mapToDomainModel(
    currentWeather: CurrentWeatherJson?, forecast: WeatherForecastJson?
): WeatherForecast {

    val location = Location(latitude = forecast?.city?.coordinates?.lat,
        longitude = forecast?.city?.coordinates?.long,
        city = forecast?.city?.name,
        country = (forecast?.city?.country)?.let { Locale("", it).displayCountry }
            ?: forecast?.city?.country,
        userLocation = false)

    val locale = Locale.getDefault()
    val gmtInt = ((forecast?.city?.timezone ?: 0L) / 3600L).toInt()
    val current = Date()

    val dateTimeFormat = SimpleDateFormat("MMM d, yyyy h:mm a", locale)
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
    val dateFormat = SimpleDateFormat("MMMM d (EE)", locale)
    val timeFormat = SimpleDateFormat("h:mm a", locale)
    val shortTimeFormat = SimpleDateFormat("ha", locale)

    val timezone = "GMT" + if (gmtInt > 0) "+%02d".format(gmtInt) else "%02d".format(gmtInt)
    TimeZone.getTimeZone(timezone).let {
        dateTimeFormat.timeZone = it
        timeFormat.timeZone = it
    }

    val updates = forecast?.forecasts?.filterNotNull()?.map {
        var date = it.dateTime?.split(" ")?.firstOrNull()
        var time = it.dateTime?.split(" ")?.lastOrNull()
        it.dateTime?.let { it1 -> inputFormat.parse(it1) }?.let { parsedDate ->
            date = dateFormat.format(parsedDate)
            time = shortTimeFormat.format(parsedDate)
        }

        WeatherUpdate(
            date = date,
            time = time,
            temperature = it.mainForecast?.temperature,
            feelsLikeTemp = it.mainForecast?.temperatureFeelsLike,
            condition = it.weatherList?.firstOrNull()?.toWeatherCondition(),
            conditionDescription = it.weatherList?.firstOrNull()?.description,
            humidity = it.mainForecast?.humidity,
            chanceOfRain = ((it.precipitation ?: 0f) * 100).roundToInt()
        )
    }?.groupBy { it.date }?.values?.toList() ?: emptyList()

    return WeatherForecast(
        location = location,
        updates = updates,
        sunrise = currentWeather?.sunriseSunset?.sunrise?.let { timeFormat.format(it * 1000L) },
        sunset = currentWeather?.sunriseSunset?.sunset?.let { timeFormat.format(it * 1000L) },
        gmtTimezone = gmtInt,
        retrievedOn = dateTimeFormat.format(current),
        current = WeatherUpdate(
            date = null,
            time = null,
            chanceOfRain = ((forecast?.forecasts?.firstOrNull()?.precipitation
                ?: 0f) * 100).roundToInt(),
            condition = currentWeather?.weather?.firstOrNull()?.toWeatherCondition(),
            conditionDescription = currentWeather?.weather?.firstOrNull()?.description,
            humidity = currentWeather?.mainForecast?.humidity,
            temperature = currentWeather?.mainForecast?.temperature,
            feelsLikeTemp = currentWeather?.mainForecast?.temperatureFeelsLike
        )
    )
}

private fun WeatherDescriptionJson?.toWeatherCondition(): WeatherCondition {
    val isDay = this?.iconId?.endsWith("n") == false
    val code = this?.weatherId
    return when {
        code == 800 && isDay -> WeatherCondition.DAY_CLEAR_SKY
        code == 800 && !isDay -> WeatherCondition.NIGHT_CLEAR_SKY

        code in 200..599 && isDay -> WeatherCondition.DAY_RAIN
        code in 200..599 && !isDay -> WeatherCondition.NIGHT_RAIN

        code in 600..699 && isDay -> WeatherCondition.DAY_SNOW
        code in 600..699 && !isDay -> WeatherCondition.NIGHT_SNOW

        code in 801..899 && isDay -> WeatherCondition.DAY_CLOUDY
        code in 801..899 && !isDay -> WeatherCondition.NIGHT_CLOUDY

        else -> WeatherCondition.UNKNOWN
    }
}
