package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ModeOfTravel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore)
    object Map : Screen("map", "Map", Icons.Filled.Map)
    object Groups : Screen("groups", "Groups", Icons.Filled.Group)
    object Trips : Screen("trips", "My Trips", Icons.Filled.ModeOfTravel)
    object Profile : Screen("profile", "Profile", Icons.Filled.AccountCircle)
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Explore,
        Screen.Map,
        Screen.Groups,
        Screen.Trips,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController, startDestination = Screen.Explore.route) {
                composable(Screen.Explore.route) { ExploreScreen(viewModel, navController) }
                composable(Screen.Map.route) { PlaceholderScreen("Map") }
                composable(Screen.Groups.route) { PlaceholderScreen("Groups") }
                composable(Screen.Trips.route) { TripsScreen(viewModel, navController) }
                composable(Screen.Profile.route) { ProfileScreen() }
                composable("event_detail/{eventId}") { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
                    if (eventId != null) {
                        EventDetailScreen(eventId, viewModel, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
