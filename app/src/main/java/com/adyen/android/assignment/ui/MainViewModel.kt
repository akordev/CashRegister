package com.adyen.android.assignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.domain.models.Venue
import com.adyen.android.assignment.domain.usecase.GetUserLocationUpdatesUseCase
import com.adyen.android.assignment.domain.usecase.GetVenuesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Handling errors is out of scope
class MainViewModel(
    private val locationUpdatesUseCase: GetUserLocationUpdatesUseCase,
    private val getVenuesUseCase: GetVenuesUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    private var lastLocation: Location? = null
    private var venuesUpdatesJob: Job? = null

    @SuppressLint("MissingPermission")
    fun handleEvents(event: Event) = when (event) {
        Event.PermissionDenied -> {
            venuesUpdatesJob?.cancel()
            venuesUpdatesJob = null
            _viewState.value = ViewState.PermissionDenied
        }

        Event.PermissionGranted -> {
            if (venuesUpdatesJob?.isCancelled != false) {
                collectVenues()
            } else {
                // Do nothing
            }
        }

        Event.GoToBackground -> {
            venuesUpdatesJob?.cancel()
            venuesUpdatesJob = null
        }
    }

    @RequiresPermission(
        allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    private fun collectVenues() {
        venuesUpdatesJob = viewModelScope.launch {
            locationUpdatesUseCase().collect { newLocation ->

                lastLocation?.let {
                    if (it.distanceTo(newLocation) < 20f) { // magic number should be discussed
                        lastLocation = newLocation
                        return@collect
                    }
                }

                _viewState.value = ViewState.Loading
                val venuesResult = getVenuesUseCase(newLocation)

                if (venuesResult.isSuccess) {
                    val venues = venuesResult
                        .getOrDefault(emptyList())
                        .sortedBy { it.distance } // maybe possible sort on server side
                    _viewState.value = ViewState.VenueList(venues)

                    lastLocation = newLocation
                }

                if (venuesResult.isFailure) {
                    // TODO lets talk how we can handle errors from UX perspective
                }
            }
        }
    }

    sealed interface ViewState {
        data class VenueList(val data: List<Venue>) : ViewState
        data object Loading : ViewState
        data object PermissionDenied : ViewState
    }

    sealed interface Event {
        data object PermissionGranted : Event
        data object PermissionDenied : Event
        data object GoToBackground : Event
    }
}