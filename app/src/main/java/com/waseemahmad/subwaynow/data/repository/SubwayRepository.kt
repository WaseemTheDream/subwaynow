package com.waseemahmad.subwaynow.data.repository

import com.waseemahmad.subwaynow.data.model.*
// TODO: Re-enable GTFS service in later round
// import com.waseemahmad.subwaynow.data.service.GTFSRealtimeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import java.io.IOException
import java.net.UnknownHostException

class SubwayRepository {
    // TODO: Re-enable GTFS service in later round
    // private val gtfsService = GTFSRealtimeService()
    
    // Simulate network connectivity issues
    private fun simulateNetworkDelay() {
        if (Math.random() < 0.1) { // 10% chance of network error
            throw UnknownHostException("No internet connection")
        }
    }
    
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
        simulateNetworkDelay()
        delay(500) // Simulate network delay
        return sampleStations.map { station ->
            station.copy(isFavorite = station.id in favoriteStationIds)
        }.sortedBy { it.name }
    }
    
    suspend fun getFavoriteStations(): List<SubwayStation> {
        return getAllStations().filter { it.isFavorite }.sortedBy { it.name }
    }
    
    suspend fun searchStations(query: String): List<SubwayStation> {
        simulateNetworkDelay()
        delay(200) // Reduced search delay for better responsiveness
        return getAllStations().filter {
            it.name.contains(query, ignoreCase = true) ||
            it.borough.contains(query, ignoreCase = true) ||
            it.lines.any { line -> line.name.contains(query, ignoreCase = true) }
        }.sortedBy { it.name }
    }
    
    suspend fun getStationArrivals(stationId: String): StationArrivals? {
        simulateNetworkDelay()
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
        
        // Simulate edge cases: sometimes no trains for some lines
        return lines.flatMap { line ->
            // 15% chance a line has no upcoming trains (late night, service disruption)
            if (Math.random() < 0.15) return@flatMap emptyList<TrainArrival>()
            
            val arrivals = mutableListOf<TrainArrival>()
            
            // Generate 1-4 arrivals per line
            val numArrivals = (1..4).random()
            repeat(numArrivals) { index ->
                val isExpress = Math.random() < 0.3 // 30% chance of express
                val baseDelay = 60 + (index * 300) // Stagger arrivals: 1min, 6min, 11min, 16min base
                val randomDelay = (Math.random() * 600).toLong() // Add 0-10 min randomness
                
                val direction = if (Math.random() > 0.5) {
                    if (line.name in listOf("N", "Q", "R", "W")) "Uptown & Queens" else "Uptown & The Bronx"
                } else {
                    "Downtown & Brooklyn"
                }
                
                val destination = getRealisticDestination(line.name, direction.contains("Uptown"), isExpress)
                
                arrivals.add(
                    TrainArrival(
                        line = line,
                        direction = if (isExpress) "$direction Express" else direction,
                        arrivalTime = currentTime + baseDelay + randomDelay,
                        destination = destination
                    )
                )
            }
            arrivals
        }.sortedBy { it.arrivalTime }
    }
    
    private fun getRealisticDestination(lineId: String, isUptown: Boolean, isExpress: Boolean): String {
        return when (lineId) {
            "1" -> if (isUptown) "Van Cortlandt Park - 242 St" else "South Ferry"
            "2" -> if (isUptown) "Wakefield - 241 St" else "Flatbush Av - Brooklyn College"
            "3" -> if (isUptown) "Harlem - 148 St" else "New Lots Av"
            "4" -> if (isUptown) {
                if (isExpress) "Woodlawn" else "125 St"
            } else "Crown Hts - Utica Av"
            "5" -> if (isUptown) "Eastchester - Dyre Av" else "Flatbush Av - Brooklyn College"
            "6" -> if (isUptown) {
                if (isExpress) "Pelham Bay Park" else "125 St"
            } else "Brooklyn Bridge - City Hall"
            "A" -> if (isUptown) "Inwood - 207 St" else "Far Rockaway - Mott Av"
            "C" -> if (isUptown) "168 St" else "Euclid Av"
            "E" -> if (isUptown) "Jamaica Center" else "World Trade Center"
            "B" -> if (isUptown) "Bedford Park Blvd" else "Brighton Beach"
            "D" -> if (isUptown) "Norwood - 205 St" else "Coney Island - Stillwell Av"
            "F" -> if (isUptown) "Jamaica - 179 St" else "Coney Island - Stillwell Av"
            "M" -> if (isUptown) "Forest Hills - 71 Av" else "Middle Village - Metropolitan Av"
            "G" -> if (isUptown) "Court Sq" else "Church Av"
            "J" -> if (isUptown) "Jamaica Center" else "Broad St"
            "Z" -> if (isUptown) "Jamaica Center" else "Broad St"
            "L" -> if (isUptown) "Canarsie - Rockaway Pkwy" else "8 Av"
            "N" -> if (isUptown) "Astoria - Ditmars Blvd" else "Coney Island - Stillwell Av"
            "Q" -> if (isUptown) "96 St - 2 Av" else "Coney Island - Stillwell Av"
            "R" -> if (isUptown) "Forest Hills - 71 Av" else "Bay Ridge - 95 St"
            "W" -> if (isUptown) "Astoria - Ditmars Blvd" else "Whitehall St - South Ferry"
            "7" -> if (isUptown) "Flushing - Main St" else "Times Sq - 42 St"
            "S" -> "Shuttle Service"
            else -> if (isUptown) "Uptown & The Bronx" else "Downtown & Brooklyn"
        }
    }
    
    fun toggleFavorite(stationId: String) {
        if (stationId in favoriteStationIds) {
            favoriteStationIds.remove(stationId)
        } else {
            favoriteStationIds.add(stationId)
        }
    }
}