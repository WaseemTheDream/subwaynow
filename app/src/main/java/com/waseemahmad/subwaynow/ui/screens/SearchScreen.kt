package com.waseemahmad.subwaynow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.waseemahmad.subwaynow.data.model.SubwayStation
import com.waseemahmad.subwaynow.data.repository.SubwayRepository
import com.waseemahmad.subwaynow.ui.components.StationCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val repository = remember { SubwayRepository() }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SubwayStation>>(emptyList()) }
    var allStations by remember { mutableStateOf<List<SubwayStation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun performSearch(query: String) {
        if (query.isBlank()) {
            searchResults = allStations
            hasSearched = true
            return
        }
        
        scope.launch {
            isLoading = true
            hasSearched = true
            hasError = false
            try {
                // Add debouncing for better UX
                kotlinx.coroutines.delay(100)
                searchResults = repository.searchStations(query)
            } catch (e: Exception) {
                hasError = true
                android.util.Log.e("SearchScreen", "Error searching stations", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadAllStations() {
        scope.launch {
            isLoading = true
            hasError = false
            try {
                allStations = repository.getAllStations()
                if (searchQuery.isBlank()) {
                    searchResults = allStations
                    hasSearched = true
                }
            } catch (e: Exception) {
                hasError = true
                android.util.Log.e("SearchScreen", "Error loading all stations", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAllStations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Search Stations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                performSearch(it)
            },
            label = { Text("Search stations, lines, or boroughs") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        searchQuery = ""
                        performSearch("")
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && !hasSearched) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (hasError && searchResults.isEmpty()) {
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
                        text = "Unable to load stations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check your internet connection and try again.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { 
                            if (searchQuery.isNotEmpty()) {
                                performSearch(searchQuery)
                            } else {
                                loadAllStations()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Retry", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        } else if (!hasSearched) {
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
                        text = "Search for NYC Subway Stations",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Find stations by name, subway line, or borough. Tap the star to add to favorites.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
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
                        text = "No Stations Found",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try searching for a different station name, line, or borough.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { station ->
                    StationCard(
                        station = station,
                        onToggleFavorite = { 
                            repository.toggleFavorite(station.id)
                            performSearch(searchQuery) // Refresh results to update favorite status
                        },
                        onClick = {
                            navController.navigate("station_detail/${station.id}")
                        }
                    )
                }
            }
        }
    }
}