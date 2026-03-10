package com.waseemahmad.subwaynow.data.api

import com.google.gson.annotations.SerializedName

// API response models that match the SubwayNow API
data class StationsResponse(
    val count: Int,
    val stations: List<ApiStation>
)

data class ApiStation(
    val id: String,
    val name: String,
    val lines: List<String>,
    val borough: String
)

data class ArrivalsResponse(
    val station: String,
    val timestamp: Long,
    val count: Int,
    val arrivals: List<ApiArrival>
)

data class ApiArrival(
    val route: String,
    val direction: String,
    @SerializedName("arrivalTime")
    val arrivalTime: Long,
    @SerializedName("minutesAway")
    val minutesAway: Int,
    @SerializedName("stopId")
    val stopId: String
)