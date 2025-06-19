package com.example.teamproject8.WJKfile.transit

import com.example.teamproject8.MYNfile.MapsPackage.GoogleDirectionsApiService
import com.example.teamproject8.MYNfile.MapsPackage.GoogleDirectionsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getTransitDirection(service: GoogleDirectionsApiService, origin: String, dest: String, moveMode: String="transit", departureTime: String, googleApiKey: String): GoogleDirectionsResponse {
    return withContext(Dispatchers.IO) {
        service.getDirections(
            origin = origin,
            destination = dest,
            mode = moveMode,    // mode,
            departureTime = departureTime,
            apiKey = googleApiKey
        )
    }
}

suspend fun getArrivalTransitDirection(service: GoogleDirectionsApiService, origin: String, dest: String, moveMode: String="transit", arrivalTime: String, googleApiKey: String): GoogleDirectionsResponse {
    return withContext(Dispatchers.IO) {
        service.getDirectionsAlt(
            origin = origin,
            destination = dest,
            mode = moveMode,
            arrivalTime = arrivalTime,
            apiKey = googleApiKey
        )
    }
}