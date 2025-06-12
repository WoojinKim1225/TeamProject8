package com.example.teamproject8.MYNfile.MapsPackage

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "transit", // driving, walking, bicycling, transit
        @Query("departure_time") departureTime: String = "now", // or Unix timestamp
        @Query("key") apiKey: String
    ): GoogleDirectionsResponse
}