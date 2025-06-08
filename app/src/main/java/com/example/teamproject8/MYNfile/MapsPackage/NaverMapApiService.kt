package com.example.week15

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapApiService {
    @GET("map-direction/v1/driving")
    suspend fun getRoute(
        @Header("x-ncp-apigw-api-key-id") clientId: String,
        @Header("x-ncp-apigw-api-key") clientSecret: String,
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Query("option") option: String = "traoptimal"
    ): DirectionResponse
}