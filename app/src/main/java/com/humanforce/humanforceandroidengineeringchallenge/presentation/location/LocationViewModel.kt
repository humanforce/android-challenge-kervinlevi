package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import com.humanforce.humanforceandroidengineeringchallenge.presentation.common.OneTimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Created by kervinlevi on 25/12/24
 */
@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: LocationRepository) :
    ViewModel() {
    var locationState = mutableStateOf(LocationState())
        private set

    val favorites = locationRepository.favoriteLocations.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    private var currentSearchJob: WeakReference<Job>? = null
    fun onAction(action: LocationAction) {
        when (action) {
            is LocationAction.SearchLocation -> {
                searchLocations(locationState.value.query)
            }

            is LocationAction.UpdateQuery -> {
                locationState.value = locationState.value.copy(query = action.query)
            }

            is LocationAction.UpdateLocation -> {
                locationRepository.updateLocation(location = action.location)
            }

            LocationAction.UseUserLocation -> {
                locationRepository.updateLocation(location = Location(userLocation = true))
            }

            is LocationAction.SaveLocation -> {
                addFavoriteLocation(action.location)
            }
        }
    }

    private fun addFavoriteLocation(location: Location) = viewModelScope.launch {
        val result = locationRepository.addFavoriteLocation(location)
        val event = if (result is Response.Success) {
            OneTimeEvent(LocationEvent.SaveFavoriteSuccessful)
        } else {
            OneTimeEvent(LocationEvent.SaveFavoriteFailed)
        }
        locationState.value = locationState.value.copy(oneTimeEvent = event)
    }

    private fun searchLocations(query: String) {
        if (currentSearchJob != null) {
            currentSearchJob?.get()?.cancel()
            currentSearchJob = null
        }

        if (query.isEmpty()) {
            locationState.value = locationState.value.copy(
                isSearchLoading = false, searchError = LocationError.EmptyQuery
            )
            return
        }

        viewModelScope.launch {
            locationState.value = locationState.value.copy(isSearchLoading = true)
            when (val result = locationRepository.searchLocations(locationState.value.query)) {
                is Response.Success -> {
                    val error = if (result.data.isEmpty()) LocationError.EmptyResult else null
                    locationState.value = locationState.value.copy(
                        isSearchLoading = false,
                        searchResults = result.data,
                        searchError = error
                    )
                }
                is Response.Error -> {
                    val exception = result.exception
                    val error = if (exception is HttpException) {
                        LocationError.HttpError("${exception.code()}: ${exception.message()}")
                    } else {
                        LocationError.NoInternet
                    }
                    locationState.value = locationState.value.copy(
                        isSearchLoading = false,
                        searchResults = emptyList(),
                        searchError = error
                    )
                }
            }
            currentSearchJob = null
        }.also {
            currentSearchJob = WeakReference(it)
        }
    }
}
