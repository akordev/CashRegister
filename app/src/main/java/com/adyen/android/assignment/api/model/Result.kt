package com.adyen.android.assignment.api.model

import com.adyen.android.assignment.domain.models.Venue

data class Result(
    val categories: List<Category>,
    val distance: Int,
    val geocode: GeoCode,
    val location: Location,
    val name: String,
    val timezone: String,
)

fun Result.toVenue(): Venue {
    return Venue(
        distance = distance,
        name = name
    )
}