package com.waseemahmad.subwaynow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.waseemahmad.subwaynow.data.model.StationArrivals
import com.waseemahmad.subwaynow.data.model.TrainArrival
import com.waseemahmad.subwaynow.data.repository.SubwayRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    stationId: String,
    navController: NavController
) {
    val repository = remember { SubwayRepository() }
    var stationArrivals by remember { mutableStateOf<StationArrivals?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadStationArrivals() {
        scope.launch {
            isLoading = true
            hasError = false
            try {
                stationArrivals = repository.getStationArrivals(stationId)
            } catch (e: Exception) {
                hasError = true
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleFavorite() {
        repository.toggleFavorite(stationId)
        stationArrivals?.let { arrivals ->
            stationArrivals = arrivals.copy(
                station = arrivals.station.copy(
                    isFavorite = !arrivals.station.isFavorite
                )
            )
        }
    }

    LaunchedEffect(stationId) {
        loadStationArrivals()
    }

    // Auto-refresh every 30 seconds
    LaunchedEffect(stationId) {
        while (true) {
            kotlinx.coroutines.delay(30000) // 30 seconds
            if (!isLoading) {
                loadStationArrivals()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stationArrivals?.station?.name ?: "Loading...",
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                // Favorite button
                stationArrivals?.let { arrivals ->
                    IconButton(onClick = ::toggleFavorite) {
                        Icon(
                            imageVector = if (arrivals.station.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (arrivals.station.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (arrivals.station.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Refresh button
                IconButton(
                    onClick = { loadStationArrivals() },
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh arrivals"
                    )
                }
            }
        )

        // Content
        when {
            isLoading && stationArrivals == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading station data",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { loadStationArrivals() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            stationArrivals != null -> {
                StationDetailContent(
                    stationArrivals = stationArrivals!!,
                    isRefreshing = isLoading,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun StationDetailContent(
    stationArrivals: StationArrivals,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Station Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Lines Serving This Station",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
                    ) {
                        stationArrivals.station.lines.forEach { line ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(line.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = line.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stationArrivals.station.borough,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Arrivals Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Arrivals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Arrivals List
        if (stationArrivals.arrivals.isEmpty()) {
            item {
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
                            text = "No upcoming trains",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Check back later for real-time arrival information.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(stationArrivals.arrivals) { arrival ->
                ArrivalItemCard(arrival = arrival)
            }
        }

        // Last updated timestamp
        item {
            Text(
                text = "Last updated: ${android.text.format.DateFormat.getTimeFormat(androidx.compose.ui.platform.LocalContext.current).format(stationArrivals.lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ArrivalItemCard(
    arrival: TrainArrival,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Line indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(arrival.line.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = arrival.line.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Destination and direction
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = arrival.destination,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = arrival.direction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Time until arrival
            val minutesUntilArrival = arrival.getMinutesUntilArrival()
            Text(
                text = when {
                    minutesUntilArrival == 0 -> "Now"
                    minutesUntilArrival == 1 -> "1 min"
                    else -> "$minutesUntilArrival min"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when {
                    minutesUntilArrival <= 2 -> MaterialTheme.colorScheme.error
                    minutesUntilArrival <= 5 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}