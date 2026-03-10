package com.waseemahmad.subwaynow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.waseemahmad.subwaynow.data.model.StationArrivals
import com.waseemahmad.subwaynow.data.repository.SubwayRepository
import com.waseemahmad.subwaynow.ui.components.StationArrivalCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val repository = remember { SubwayRepository() }
    var favoriteStationArrivals by remember { mutableStateOf<List<StationArrivals>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadFavoriteArrivals() {
        scope.launch {
            isLoading = true
            try {
                val favorites = repository.getFavoriteStations()
                val arrivals = favorites.mapNotNull { station ->
                    repository.getStationArrivals(station.id)
                }
                favoriteStationArrivals = arrivals
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFavoriteArrivals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorite Stations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { loadFavoriteArrivals() },
                enabled = !isLoading
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh arrivals"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (favoriteStationArrivals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Favorite Stations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add stations to your favorites in the Search tab to see real-time arrivals here.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteStationArrivals) { stationArrivals ->
                    StationArrivalCard(
                        stationArrivals = stationArrivals,
                        onToggleFavorite = { 
                            repository.toggleFavorite(stationArrivals.station.id)
                            loadFavoriteArrivals()
                        }
                    )
                }
            }
        }
    }
}