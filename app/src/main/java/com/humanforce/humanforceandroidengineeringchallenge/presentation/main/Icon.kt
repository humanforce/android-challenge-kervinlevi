package com.humanforce.humanforceandroidengineeringchallenge.presentation.main

import androidx.compose.ui.graphics.Color
import com.humanforce.humanforceandroidengineeringchallenge.R
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.WeatherCondition

/**
 * Created by kervinlevi on 24/12/24
 */
fun WeatherCondition?.toSmallIconResource(): Int {
    return when (this) {
        WeatherCondition.DAY_CLEAR_SKY -> R.drawable.ic_day_clear_sky
        WeatherCondition.DAY_CLOUDY -> R.drawable.ic_day_cloudy
        WeatherCondition.DAY_RAIN -> R.drawable.ic_day_rain
        WeatherCondition.DAY_SNOW -> R.drawable.ic_day_snow
        WeatherCondition.NIGHT_CLEAR_SKY -> R.drawable.ic_night_clear_sky
        WeatherCondition.NIGHT_CLOUDY -> R.drawable.ic_night_cloudy
        WeatherCondition.NIGHT_RAIN -> R.drawable.ic_night_rain
        WeatherCondition.NIGHT_SNOW -> R.drawable.ic_night_snow
        else -> R.drawable.ic_unknown
    }
}

fun WeatherCondition?.toGradientColors(): List<Color> {
    return when (this) {
        WeatherCondition.DAY_CLEAR_SKY -> listOf(OrangeLighten4, YellowLighten4)
        WeatherCondition.DAY_CLOUDY -> listOf(BlueLighten3, CyanLighten5)
        WeatherCondition.DAY_RAIN -> listOf(BlueLighten3, CyanLighten5)
        WeatherCondition.DAY_SNOW -> listOf(BlueLighten3, CyanLighten5)
        WeatherCondition.NIGHT_CLEAR_SKY -> listOf(DeepPurpleLighten3, PinkLighten5)
        WeatherCondition.NIGHT_CLOUDY -> listOf(DeepPurpleLighten3, PinkLighten5)
        WeatherCondition.NIGHT_RAIN -> listOf(IndigoLighten2, BlueLighten5)
        WeatherCondition.NIGHT_SNOW -> listOf(IndigoLighten2, BlueLighten5)
        else -> listOf(BlueLighten3, CyanLighten5)
    }
}
