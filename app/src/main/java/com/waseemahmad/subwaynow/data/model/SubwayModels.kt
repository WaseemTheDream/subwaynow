package com.waseemahmad.subwaynow.data.model

import androidx.compose.ui.graphics.Color
import com.waseemahmad.subwaynow.ui.theme.*

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