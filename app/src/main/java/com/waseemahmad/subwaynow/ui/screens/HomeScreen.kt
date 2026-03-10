package com.waseemahmad.subwaynow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.waseemahmad.subwaynow.data.model.StationArrivals
import com.waseemahmad.subwaynow.data.repository.SubwayRepository
import com.waseemahmad.subwaynow.ui.components.StationArrivalCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val repository = remember { SubwayRepository() }
    var favoriteStationArrivals by remember { mutableStateOf<List<StationArrivals>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var serviceAlert by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadFavoriteArrivals() {
        scope.launch {
            isLoading = true
            hasError = false
            try {
                // Simulate service alerts (15% chance)
                serviceAlert = if (Math.random() < 0.15) {
                    val alerts = listOf(
                        "4/5/6 lines: Delays due to signal problems at Union Square",
                        "L train: Weekend service changes in effect",
                        "N/Q/R/W: Minor delays system-wide due to increased ridership",
                        "A/C lines: Service change due to track maintenance", 
                        "7 line: Good service with minor delays"
                    )
                    alerts.random()
                } else null
                
                val favorites = repository.getFavoriteStations()
                val arrivals = favorites.mapNotNull { station ->
                    repository.getStationArrivals(station.id)
                }
                favoriteStationArrivals = arrivals
            } catch (e: Exception) {
                hasError = true
                // Log errors for debugging
                android.util.Log.e("HomeScreen", "Error loading favorites", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFavoriteArrivals()
    }

    // Auto-refresh every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30000) // 30 seconds
            if (!isLoading) {
                loadFavoriteArrivals()
            }
        }
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

        // Service alerts banner
        serviceAlert?.let { alert ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Service Alert",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = alert,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading && favoriteStationArrivals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (hasError && favoriteStationArrivals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Unable to load arrivals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please check your internet connection. The app requires network access to load real-time train arrivals.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { loadFavoriteArrivals() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Retry", color = MaterialTheme.colorScheme.onError)
                    }
                }
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
                        },
                        onClick = {
                            navController.navigate("station_detail/${stationArrivals.station.id}")
                        }
                    )
                }
            }
        }
    }
}