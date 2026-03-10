package com.waseemahmad.subwaynow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.waseemahmad.subwaynow.data.model.StationArrivals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationArrivalCard(
    stationArrivals: StationArrivals,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stationArrivals.station.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stationArrivals.station.borough,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (stationArrivals.station.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (stationArrivals.station.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (stationArrivals.station.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (stationArrivals.arrivals.isEmpty()) {
                Text(
                    text = "No upcoming arrivals",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            } else {
                // Group arrivals by line and show next few
                val arrivalsByLine = stationArrivals.arrivals
                    .groupBy { it.line }
                    .mapValues { (_, arrivals) -> arrivals.sortedBy { it.arrivalTime }.take(3) }
                
                arrivalsByLine.forEach { (line, arrivals) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(line.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = line.name,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            arrivals.forEach { arrival ->
                                val minutesUntil = arrival.getMinutesUntilArrival()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${arrival.direction} - ${arrival.destination}",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = when {
                                            minutesUntil <= 0 -> "Now"
                                            minutesUntil == 1 -> "1 min"
                                            else -> "${minutesUntil} mins"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = when {
                                            minutesUntil <= 1 -> MaterialTheme.colorScheme.error
                                            minutesUntil <= 5 -> Color(0xFFFF9800) // Orange
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    if (line != arrivalsByLine.keys.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Last updated: ${java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(stationArrivals.lastUpdated))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}