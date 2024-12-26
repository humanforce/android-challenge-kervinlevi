package com.humanforce.humanforceandroidengineeringchallenge.presentation.common

import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location

/**
 * Created by kervinlevi on 26/12/24
 */

fun Location?.getDisplayText(max: Int = 3, default: String = ""): String {
    if (this == null) return default

    val list = listOfNotNull(city, state, country)
    return list.take(max).joinToString()
}
