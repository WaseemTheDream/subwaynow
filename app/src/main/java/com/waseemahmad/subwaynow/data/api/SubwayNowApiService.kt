package com.waseemahmad.subwaynow.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SubwayNowApiService {
    
    @GET("api/stations")
    suspend fun getStations(): Response<StationsResponse>
    
    @GET("api/stations")
    suspend fun searchStations(@Query("q") query: String): Response<StationsResponse>
    
    @GET("api/stations")
    suspend fun getStationsByLine(@Query("line") line: String): Response<StationsResponse>
    
    @GET("api/stations") 
    suspend fun getStationsByBorough(@Query("borough") borough: String): Response<StationsResponse>
    
    @GET("api/arrivals")
    suspend fun getArrivals(@Query("station") stationId: String): Response<ArrivalsResponse>
}