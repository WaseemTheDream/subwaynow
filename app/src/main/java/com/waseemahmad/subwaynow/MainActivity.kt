package com.waseemahmad.subwaynow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.waseemahmad.subwaynow.ui.navigation.SubwayNowBottomNavBar
import com.waseemahmad.subwaynow.ui.navigation.SubwayNowNavGraph
import com.waseemahmad.subwaynow.ui.theme.SubwayNowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            SubwayNowTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        SubwayNowBottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    SubwayNowNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}