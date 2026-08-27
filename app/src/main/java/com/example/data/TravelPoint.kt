package com.example.data

import com.google.android.gms.maps.model.LatLng

data class TravelPoint(
    val id: String,
    val title: String,
    val description: String,
    val location: LatLng
)

val occitanieTouristSpots = listOf(
    TravelPoint("1", "Cité de Carcassonne", "Medieval fortress with 3km of walls and 52 towers.", LatLng(43.2063, 2.3636)),
    TravelPoint("2", "Albi Cathedral", "The largest brick building in the world.", LatLng(43.9285, 2.1426)),
    TravelPoint("3", "Rocamadour", "A sacred village dramatically perched on a cliff.", LatLng(44.7994, 1.6181)),
    TravelPoint("4", "Pont du Gard", "An ancient Roman aqueduct bridge built in the first century AD.", LatLng(43.9530, 4.5348)),
    TravelPoint("5", "Nîmes Amphitheatre", "One of the best-preserved Roman arenas in the world.", LatLng(43.8349, 4.3596)),
    TravelPoint("6", "Capitole de Toulouse", "The heart of Toulouse housing the city hall and opera.", LatLng(43.6045, 1.4440))
)
