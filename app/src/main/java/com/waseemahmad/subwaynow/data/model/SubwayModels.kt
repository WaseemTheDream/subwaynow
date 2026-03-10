package com.waseemahmad.subwaynow.data.model

import androidx.compose.ui.graphics.Color
import com.waseemahmad.subwaynow.ui.theme.*
import com.waseemahmad.subwaynow.data.api.ApiStation
import com.waseemahmad.subwaynow.data.api.ApiArrival

data class SubwayStation(
    val id: String,
    val name: String,
    val lines: List<SubwayLine>,
    val borough: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    var isFavorite: Boolean = false
)

data class SubwayLine(
    val id: String,
    val name: String,
    val color: Color
) {
    companion object {
        val LINE_1 = SubwayLine("1", "1", Number123_Red)
        val LINE_2 = SubwayLine("2", "2", Number123_Red)
        val LINE_3 = SubwayLine("3", "3", Number123_Red)
        val LINE_4 = SubwayLine("4", "4", Number456_Green)
        val LINE_5 = SubwayLine("5", "5", Number456_Green)
        val LINE_6 = SubwayLine("6", "6", Number456_Green)
        val LINE_7 = SubwayLine("7", "7", Number7_Purple)
        val LINE_A = SubwayLine("A", "A", ACE_Blue)
        val LINE_C = SubwayLine("C", "C", ACE_Blue)
        val LINE_E = SubwayLine("E", "E", ACE_Blue)
        val LINE_B = SubwayLine("B", "B", BDFM_Orange)
        val LINE_D = SubwayLine("D", "D", BDFM_Orange)
        val LINE_F = SubwayLine("F", "F", BDFM_Orange)
        val LINE_M = SubwayLine("M", "M", BDFM_Orange)
        val LINE_G = SubwayLine("G", "G", G_Green)
        val LINE_J = SubwayLine("J", "J", JZ_Brown)
        val LINE_Z = SubwayLine("Z", "Z", JZ_Brown)
        val LINE_L = SubwayLine("L", "L", L_Gray)
        val LINE_N = SubwayLine("N", "N", NQR_Yellow)
        val LINE_Q = SubwayLine("Q", "Q", NQR_Yellow)
        val LINE_R = SubwayLine("R", "R", NQR_Yellow)
        val LINE_W = SubwayLine("W", "W", NQR_Yellow)
        val LINE_S = SubwayLine("S", "S", S_DarkGray)
        val LINE_SIR = SubwayLine("SIR", "SIR", SIR_Blue)

        val ALL_LINES = listOf(
            LINE_1, LINE_2, LINE_3, LINE_4, LINE_5, LINE_6, LINE_7,
            LINE_A, LINE_C, LINE_E, LINE_B, LINE_D, LINE_F, LINE_M,
            LINE_G, LINE_J, LINE_Z, LINE_L, LINE_N, LINE_Q, LINE_R, LINE_W,
            LINE_S, LINE_SIR
        )
    }
}

data class TrainArrival(
    val line: SubwayLine,
    val direction: String,
    val arrivalTime: Long, // Unix timestamp
    val destination: String
) {
    fun getMinutesUntilArrival(): Int {
        val currentTime = System.currentTimeMillis() / 1000
        val timeDiff = arrivalTime - currentTime
        return (timeDiff / 60).toInt().coerceAtLeast(0)
    }
}

data class StationArrivals(
    val station: SubwayStation,
    val arrivals: List<TrainArrival>,
    val lastUpdated: Long = System.currentTimeMillis()
)

// Extension functions to convert API models to UI models
fun ApiStation.toSubwayStation(favoriteStationIds: Set<String> = emptySet()): SubwayStation {
    val subwayLines = lines.mapNotNull { lineId ->
        SubwayLine.ALL_LINES.find { it.id == lineId }
    }
    
    return SubwayStation(
        id = id,
        name = name,
        lines = subwayLines,
        borough = borough,
        isFavorite = id in favoriteStationIds
    )
}

fun ApiArrival.toTrainArrival(): TrainArrival {
    val subwayLine = SubwayLine.ALL_LINES.find { it.id == route } 
        ?: SubwayLine("UNKNOWN", route, Color.Gray)
    
    return TrainArrival(
        line = subwayLine,
        direction = direction,
        arrivalTime = arrivalTime,
        destination = getDestinationForRoute(route, direction.contains("Uptown") || direction.contains("Queens"))
    )
}

private fun getDestinationForRoute(route: String, isUptown: Boolean): String {
    return when (route) {
        "1" -> if (isUptown) "Van Cortlandt Park-242 St" else "South Ferry"
        "2" -> if (isUptown) "Wakefield-241 St" else "Flatbush Av-Brooklyn College"
        "3" -> if (isUptown) "Harlem-148 St" else "New Lots Av"
        "4" -> if (isUptown) "Woodlawn" else "Crown Hts-Utica Av"
        "5" -> if (isUptown) "Eastchester-Dyre Av" else "Flatbush Av-Brooklyn College"
        "6" -> if (isUptown) "Pelham Bay Park" else "Brooklyn Bridge-City Hall"
        "7" -> if (isUptown) "Flushing-Main St" else "Times Sq-42 St"
        "A" -> if (isUptown) "Inwood-207 St" else "Far Rockaway-Mott Av"
        "C" -> if (isUptown) "168 St" else "Euclid Av"
        "E" -> if (isUptown) "Jamaica Center" else "World Trade Center"
        "B" -> if (isUptown) "Bedford Park Blvd" else "Brighton Beach"
        "D" -> if (isUptown) "Norwood-205 St" else "Coney Island-Stillwell Av"
        "F" -> if (isUptown) "Jamaica-179 St" else "Coney Island-Stillwell Av"
        "M" -> if (isUptown) "Forest Hills-71 Av" else "Middle Village-Metropolitan Av"
        "G" -> if (isUptown) "Court Sq" else "Church Av"
        "J" -> if (isUptown) "Jamaica Center" else "Broad St"
        "Z" -> if (isUptown) "Jamaica Center" else "Broad St"
        "L" -> if (isUptown) "Canarsie-Rockaway Pkwy" else "8 Av"
        "N" -> if (isUptown) "Astoria-Ditmars Blvd" else "Coney Island-Stillwell Av"
        "Q" -> if (isUptown) "96 St-2 Av" else "Coney Island-Stillwell Av"
        "R" -> if (isUptown) "Forest Hills-71 Av" else "Bay Ridge-95 St"
        "W" -> if (isUptown) "Astoria-Ditmars Blvd" else "Whitehall St-South Ferry"
        "S" -> "Shuttle Service"
        "SIR" -> if (isUptown) "St. George Terminal" else "Tottenville"
        else -> if (isUptown) "Uptown & Queens" else "Downtown & Brooklyn"
    }
}