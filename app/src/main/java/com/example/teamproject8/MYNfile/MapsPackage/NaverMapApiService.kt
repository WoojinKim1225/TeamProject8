//package com.example.teamproject8.MYNfile.MapsPackage
//
//import com.example.teamproject8.MYNfile.MapsPackage.DirectionResponse
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Query
//
//interface NaverMapApiService {
//
//    @GET("map-direction/v1/driving")
//    suspend fun getDrivingRoute(
//        @Header("x-ncp-apigw-api-key-id") clientId: String,
//        @Header("x-ncp-apigw-api-key") clientSecret: String,
//        @Query("start") start: String,
//        @Query("goal") goal: String,
//        @Query("option") option: String = "traoptimal"
//    ): DirectionResponse
//
//    @GET("map-direction/v1/walking")
//    suspend fun getWalkingRoute(
//        @Header("x-ncp-apigw-api-key-id") clientId: String,
//        @Header("x-ncp-apigw-api-key") clientSecret: String,
//        @Query("start") start: String,
//        @Query("goal") goal: String
//    ): DirectionResponse
//
//    @GET("map-direction-15/v1/bicycling")
//    suspend fun getBicycleRoute(
//        @Header("x-ncp-apigw-api-key-id") clientId: String,
//        @Header("x-ncp-apigw-api-key") clientSecret: String,
//        @Query("start") start: String,
//        @Query("goal") goal: String
//    ): DirectionResponse
//}