package com.waseemahmad.subwaynow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.waseemahmad.subwaynow.ui.screens.FavoritesScreen
import com.waseemahmad.subwaynow.ui.screens.HomeScreen
import com.waseemahmad.subwaynow.ui.screens.SearchScreen

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Filled.Home),
    Favorites("favorites", "Favorites", Icons.Filled.Star),
    Search("search", "Search", Icons.Filled.Search)
}

@Composable
fun SubwayNowNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
    }
}

@Composable
fun SubwayNowBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        Screen.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}