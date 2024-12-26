package com.humanforce.humanforceandroidengineeringchallenge.presentation.common

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.humanforce.humanforceandroidengineeringchallenge.R
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.TemperatureUnit
import kotlin.math.roundToInt

/**
 * Created by kervinlevi on 26/12/24
 */

fun Location?.getDisplayText(max: Int = 3, default: String = ""): String {
    if (this == null) return default

    val list = listOfNotNull(city, state, country)
    return list.take(max).joinToString()
}

fun Context.getTemperature(value: Float?, unit: TemperatureUnit, default: String = ""): String {
    if (value == null) return default
    val intValue = value.roundToInt()
    return when(unit) {
        TemperatureUnit.CELSIUS -> getString(R.string.celsius, intValue)
        TemperatureUnit.FAHRENHEIT -> getString(R.string.fahrenheit, intValue)
        TemperatureUnit.KELVIN -> getString(R.string.kelvin, intValue)
    }
}
