package com.waseemahmad.subwaynow.data.repository

import com.waseemahmad.subwaynow.data.model.*
import com.waseemahmad.subwaynow.data.api.SubwayNowApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import java.io.IOException
import java.net.UnknownHostException

class SubwayRepository {
    private val apiClient = SubwayNowApiClient.getInstance()
    private val apiService = apiClient.apiService
    
    // Cache for stations to reduce API calls
    private var stationsCache: List<SubwayStation>? = null
    private var cacheTimestamp: Long = 0
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes
    
    // Favorites stored locally (in a real app, this would be in SharedPreferences or database)
    private val favoriteStationIds = mutableSetOf<String>()
    
    suspend fun getAllStations(): List<SubwayStation> = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            val currentTime = System.currentTimeMillis()
            if (stationsCache != null && (currentTime - cacheTimestamp) < CACHE_DURATION) {
                return@withContext stationsCache!!.map { station ->
                    station.copy(isFavorite = station.id in favoriteStationIds)
                }.sortedBy { it.name }
            }
            
            // Fetch from API
            val response = apiService.getStations()
            if (response.isSuccessful) {
                val stationsResponse = response.body()
                if (stationsResponse != null) {
                    val stations = stationsResponse.stations.map { apiStation ->
                        apiStation.toSubwayStation(favoriteStationIds)
                    }.sortedBy { it.name }
                    
                    // Update cache
                    stationsCache = stations
                    cacheTimestamp = currentTime
                    
                    Log.d("SubwayRepository", "Loaded ${stations.size} stations from API")
                    return@withContext stations
                } else {
                    throw IOException("Empty response from API")
                }
            } else {
                throw IOException("API call failed: ${response.code()} ${response.message()}")
            }
        } catch (e: UnknownHostException) {
            Log.e("SubwayRepository", "No internet connection", e)
            throw IOException("No internet connection. Please check your network.")
        } catch (e: Exception) {
            Log.e("SubwayRepository", "Error fetching stations", e)
            throw IOException("Failed to load stations: ${e.message}")
        }
    }
    
    suspend fun getFavoriteStations(): List<SubwayStation> {
        return getAllStations().filter { it.isFavorite }.sortedBy { it.name }
    }
    
    suspend fun searchStations(query: String): List<SubwayStation> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext getAllStations()
            }
            
            val response = apiService.searchStations(query.trim())
            if (response.isSuccessful) {
                val stationsResponse = response.body()
                if (stationsResponse != null) {
                    val stations = stationsResponse.stations.map { apiStation ->
                        apiStation.toSubwayStation(favoriteStationIds)
                    }.sortedBy { it.name }
                    
                    Log.d("SubwayRepository", "Search '$query' returned ${stations.size} stations")
                    return@withContext stations
                } else {
                    throw IOException("Empty search response from API")
                }
            } else {
                throw IOException("Search API call failed: ${response.code()} ${response.message()}")
            }
        } catch (e: UnknownHostException) {
            Log.e("SubwayRepository", "No internet connection for search", e)
            throw IOException("No internet connection. Please check your network.")
        } catch (e: Exception) {
            Log.e("SubwayRepository", "Error searching stations", e)
            throw IOException("Search failed: ${e.message}")
        }
    }
    
    suspend fun getStationArrivals(stationId: String): StationArrivals? = withContext(Dispatchers.IO) {
        try {
            // First get station info
            val stations = getAllStations()
            val station = stations.find { it.id == stationId } ?: return@withContext null
            
            // Then get arrivals
            val response = apiService.getArrivals(stationId)
            if (response.isSuccessful) {
                val arrivalsResponse = response.body()
                if (arrivalsResponse != null) {
                    val arrivals = arrivalsResponse.arrivals.map { apiArrival ->
                        apiArrival.toTrainArrival()
                    }.sortedBy { it.arrivalTime }
                    
                    Log.d("SubwayRepository", "Loaded ${arrivals.size} arrivals for station $stationId")
                    
                    return@withContext StationArrivals(
                        station = station,
                        arrivals = arrivals,
                        lastUpdated = System.currentTimeMillis()
                    )
                } else {
                    Log.w("SubwayRepository", "Empty arrivals response for station $stationId")
                    return@withContext StationArrivals(
                        station = station,
                        arrivals = emptyList(),
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            } else {
                throw IOException("Arrivals API call failed: ${response.code()} ${response.message()}")
            }
        } catch (e: UnknownHostException) {
            Log.e("SubwayRepository", "No internet connection for arrivals", e)
            throw IOException("No internet connection. Please check your network.")
        } catch (e: Exception) {
            Log.e("SubwayRepository", "Error fetching arrivals for station $stationId", e)
            throw IOException("Failed to load arrivals: ${e.message}")
        }
    }
    
    fun toggleFavorite(stationId: String) {
        if (stationId in favoriteStationIds) {
            favoriteStationIds.remove(stationId)
            Log.d("SubwayRepository", "Removed station $stationId from favorites")
        } else {
            favoriteStationIds.add(stationId)
            Log.d("SubwayRepository", "Added station $stationId to favorites")
        }
        
        // Update cache to reflect favorite changes
        stationsCache = stationsCache?.map { station ->
            if (station.id == stationId) {
                station.copy(isFavorite = stationId in favoriteStationIds)
            } else {
                station
            }
        }
    }
    
    fun clearCache() {
        stationsCache = null
        cacheTimestamp = 0
        Log.d("SubwayRepository", "Cleared stations cache")
    }
}