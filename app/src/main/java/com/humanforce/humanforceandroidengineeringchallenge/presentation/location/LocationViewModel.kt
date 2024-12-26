package com.humanforce.humanforceandroidengineeringchallenge.presentation.location

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humanforce.humanforceandroidengineeringchallenge.domain.location.LocationRepository
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Location
import com.humanforce.humanforceandroidengineeringchallenge.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var currentSearchJob: WeakReference<Job>? = null
    fun onAction(action: LocationAction) {
        when (action) {
            LocationAction.PermissionGranted -> {

            }

            is LocationAction.SearchLocation -> {
                searchLocations(locationState.value.query)
            }

            LocationAction.ShowPermissionsRationale -> {

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
        }
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
            if (query.isEmpty()) return@launch

            locationState.value = locationState.value.copy(isSearchLoading = true)
            val result = locationRepository.searchLocations(locationState.value.query)

            if (result is Response.Success) {
                val error = if (result.data.isEmpty()) LocationError.EmptyResult else null
                locationState.value = locationState.value.copy(
                    isSearchLoading = false,
                    searchResults = result.data,
                    searchError = error
                )
            } else {
                // show error
            }
            currentSearchJob = null
        }.also {
            currentSearchJob = WeakReference(it)
        }
    }
}
