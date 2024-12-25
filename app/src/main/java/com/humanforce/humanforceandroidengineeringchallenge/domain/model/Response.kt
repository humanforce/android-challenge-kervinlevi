package com.humanforce.humanforceandroidengineeringchallenge.domain.model

/**
 * Created by kervinlevi on 24/12/24
 */
sealed interface Response<T> {
    data class Success<T>(val data: T) : Response<T>
    data class Error<T>(val exception: Exception) : Response<T>
}
