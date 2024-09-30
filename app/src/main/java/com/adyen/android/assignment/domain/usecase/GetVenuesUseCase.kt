package com.adyen.android.assignment.domain.usecase

import android.location.Location
import com.adyen.android.assignment.DispatcherProvider
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.toVenue
import com.adyen.android.assignment.domain.models.Venue
import kotlinx.coroutines.withContext

class GetVenuesUseCase(
    private val api: PlacesService, // could be repository with several data sources
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(location: Location): Result<List<Venue>> =
        withContext(dispatcherProvider.io) {
            try {
                val query = VenueRecommendationsQueryBuilder().setLatitudeLongitude(
                        location.latitude,
                        location.longitude
                    ).build()

                val venuesCall = api.getVenueRecommendations(query).results.map { it.toVenue() }

                return@withContext Result.success(venuesCall)
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
}