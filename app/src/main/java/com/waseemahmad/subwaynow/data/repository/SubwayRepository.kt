package com.waseemahmad.subwaynow.data.repository

import com.waseemahmad.subwaynow.data.model.*
// TODO: Re-enable GTFS service in later round
// import com.waseemahmad.subwaynow.data.service.GTFSRealtimeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class SubwayRepository {
    // TODO: Re-enable GTFS service in later round
    // private val gtfsService = GTFSRealtimeService()
    
    // Sample stations for demonstration
    private val sampleStations = listOf(
        SubwayStation(
            id = "R16",
            name = "Times Sq - 42 St",
            lines = listOf(SubwayLine.LINE_N, SubwayLine.LINE_Q, SubwayLine.LINE_R, SubwayLine.LINE_W, SubwayLine.LINE_S, SubwayLine.LINE_1, SubwayLine.LINE_2, SubwayLine.LINE_3, SubwayLine.LINE_7),
            borough = "Manhattan"
        ),
        SubwayStation(
            id = "L01",
            name = "14 St - Union Sq",
            lines = listOf(SubwayLine.LINE_L, SubwayLine.LINE_N, SubwayLine.LINE_Q, SubwayLine.LINE_R, SubwayLine.LINE_W, SubwayLine.LINE_4, SubwayLine.LINE_5, SubwayLine.LINE_6),
            borough = "Manhattan"
        ),
        SubwayStation(
            id = "A02",
            name = "59 St - Columbus Circle",
            lines = listOf(SubwayLine.LINE_A, SubwayLine.LINE_C, SubwayLine.LINE_B, SubwayLine.LINE_D, SubwayLine.LINE_1),
            borough = "Manhattan"
        ),
        SubwayStation(
            id = "D01",
            name = "Atlantic Av - Barclays Ctr",
            lines = listOf(SubwayLine.LINE_B, SubwayLine.LINE_D, SubwayLine.LINE_N, SubwayLine.LINE_Q, SubwayLine.LINE_R, SubwayLine.LINE_W, SubwayLine.LINE_2, SubwayLine.LINE_3, SubwayLine.LINE_4, SubwayLine.LINE_5),
            borough = "Brooklyn"
        ),
        SubwayStation(
            id = "F01", 
            name = "Herald Sq - 34 St",
            lines = listOf(SubwayLine.LINE_B, SubwayLine.LINE_D, SubwayLine.LINE_F, SubwayLine.LINE_M, SubwayLine.LINE_N, SubwayLine.LINE_Q, SubwayLine.LINE_R, SubwayLine.LINE_W),
            borough = "Manhattan"
        ),
        SubwayStation(
            id = "G01",
            name = "Grand Central - 42 St",
            lines = listOf(SubwayLine.LINE_4, SubwayLine.LINE_5, SubwayLine.LINE_6, SubwayLine.LINE_7),
            borough = "Manhattan"
        )
    )
    
    private val favoriteStationIds = mutableSetOf<String>()
    
    suspend fun getAllStations(): List<SubwayStation> {
        delay(500) // Simulate network delay
        return sampleStations.map { station ->
            station.copy(isFavorite = station.id in favoriteStationIds)
        }
    }
    
    suspend fun getFavoriteStations(): List<SubwayStation> {
        return getAllStations().filter { it.isFavorite }
    }
    
    suspend fun searchStations(query: String): List<SubwayStation> {
        delay(300) // Simulate search delay
        return getAllStations().filter {
            it.name.contains(query, ignoreCase = true) ||
            it.borough.contains(query, ignoreCase = true) ||
            it.lines.any { line -> line.name.contains(query, ignoreCase = true) }
        }
    }
    
    suspend fun getStationArrivals(stationId: String): StationArrivals? {
        val station = sampleStations.find { it.id == stationId } ?: return null
        val stationWithFavorite = station.copy(isFavorite = station.id in favoriteStationIds)
        
        // TODO: Add real GTFS-RT integration in later round
        // For now, use mock data to demonstrate functionality
        delay(1000) // Simulate API call
        val mockArrivals = generateMockArrivals(station.lines)
        
        return StationArrivals(
            station = stationWithFavorite,
            arrivals = mockArrivals
        )
    }
    
    private fun generateMockArrivals(lines: List<SubwayLine>): List<TrainArrival> {
        val currentTime = System.currentTimeMillis() / 1000
        return lines.flatMap { line ->
            listOf(
                TrainArrival(
                    line = line,
                    direction = if (Math.random() > 0.5) "Uptown" else "Downtown", 
                    arrivalTime = currentTime + (Math.random() * 1800).toLong(), // Next 0-30 mins
                    destination = when (line.name) {
                        "1", "2", "3" -> if (Math.random() > 0.5) "Uptown & The Bronx" else "Downtown & Brooklyn"
                        "4", "5", "6" -> if (Math.random() > 0.5) "Uptown & The Bronx" else "Downtown & Brooklyn"
                        "N", "Q", "R", "W" -> if (Math.random() > 0.5) "Manhattan" else "Brooklyn"
                        else -> if (Math.random() > 0.5) "Manhattan" else "Brooklyn"
                    }
                ),
                TrainArrival(
                    line = line,
                    direction = if (Math.random() > 0.5) "Uptown" else "Downtown",
                    arrivalTime = currentTime + (Math.random() * 2400).toLong(), // Next 0-40 mins
                    destination = when (line.name) {
                        "1", "2", "3" -> if (Math.random() > 0.5) "Uptown & The Bronx" else "Downtown & Brooklyn"
                        "4", "5", "6" -> if (Math.random() > 0.5) "Uptown & The Bronx" else "Downtown & Brooklyn"
                        "N", "Q", "R", "W" -> if (Math.random() > 0.5) "Manhattan" else "Brooklyn"
                        else -> if (Math.random() > 0.5) "Manhattan" else "Brooklyn"
                    }
                )
            )
        }.sortedBy { it.arrivalTime }
    }
    
    fun toggleFavorite(stationId: String) {
        if (stationId in favoriteStationIds) {
            favoriteStationIds.remove(stationId)
        } else {
            favoriteStationIds.add(stationId)
        }
    }
}