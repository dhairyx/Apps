package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import com.example.network.DailyWeather
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker as GoogleMarker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import android.location.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(eventId: Int, viewModel: MainViewModel, navController: NavController) {
    val eventWithOrg by viewModel.getEventById(eventId).collectAsStateWithLifecycle(initialValue = null)
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(eventWithOrg) {
        eventWithOrg?.let {
            viewModel.fetchWeather(it.event.location)
        }
    }

    if (eventWithOrg == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val e = eventWithOrg!!.event
    val organizer = eventWithOrg!!.organizer
    val imageResId = context.resources.getIdentifier(e.imageResName, "drawable", context.packageName)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(e.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                PaddingValues(16.dp)
                Button(
                    onClick = {
                        if (e.isJoined) viewModel.leaveEvent(e.id)
                        else viewModel.joinEvent(e.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                ) {
                    if (e.isJoined) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Joined")
                    } else {
                        Text("Join Trip")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.ic_launcher_background),
                contentDescription = e.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = e.category.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = e.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Info Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Date", value = e.date)
                    InfoItem(label = "Location", value = e.location)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Cost", value = e.estimatedCost)
                    InfoItem(label = "Difficulty", value = e.difficulty)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Participants", value = "${e.currentParticipants}/${e.maxParticipants}")
                    InfoItem(label = "Organizer", value = organizer.name)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Trip Information",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = e.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "3-Day Weather Forecast",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (weatherState == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    WeatherForecastSection(weather = weatherState!!)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Location Map",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val locationCoordinates by viewModel.locationCoordinates.collectAsStateWithLifecycle()
                if (locationCoordinates == null) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    LocationMap(
                        latitude = locationCoordinates!!.first,
                        longitude = locationCoordinates!!.second,
                        title = e.title
                    )
                }
                
                Spacer(modifier = Modifier.height(100.dp)) // padding for bottom bar
            }
        }
    }
}

@Composable
fun WeatherForecastSection(weather: DailyWeather) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0 until minOf(3, weather.time.size)) {
            WeatherDayCard(
                date = weather.time[i],
                maxTemp = weather.maxTemp[i],
                minTemp = weather.minTemp[i],
                weatherCode = weather.weatherCode[i]
            )
        }
    }
}

@Composable
fun WeatherDayCard(date: String, maxTemp: Double, minTemp: Double, weatherCode: Int) {
    // simplified formatting
    val day = date.substring(date.length - 5) // "MM-DD" or similar if format is "YYYY-MM-DD"
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = getWeatherEmoji(weatherCode), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${maxTemp.toInt()}° / ${minTemp.toInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getWeatherEmoji(code: Int): String {
    return when (code) {
        0 -> "☀️" // Clear sky
        1, 2, 3 -> "⛅" // Mainly clear, partly cloudy, and overcast
        45, 48 -> "🌫️" // Fog and depositing rime fog
        51, 53, 55 -> "🌧️" // Drizzle: Light, moderate, and dense intensity
        56, 57 -> "🌧️" // Freezing Drizzle: Light and dense intensity
        61, 63, 65 -> "🌧️" // Rain: Slight, moderate and heavy intensity
        66, 67 -> "🌧️" // Freezing Rain: Light and heavy intensity
        71, 73, 75 -> "❄️" // Snow fall: Slight, moderate, and heavy intensity
        77 -> "❄️" // Snow grains
        80, 81, 82 -> "🌧️" // Rain showers: Slight, moderate, and violent
        85, 86 -> "❄️" // Snow showers slight and heavy
        95 -> "⛈️" // Thunderstorm: Slight or moderate
        96, 99 -> "⛈️" // Thunderstorm with slight and heavy hail
        else -> "❓"
    }
}

@Composable
fun LocationMap(latitude: Double, longitude: Double, title: String) {
    val location = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }

    // Mock User Location (Toulouse, Occitanie)
    val userLat = 43.6047
    val userLon = 1.4442
    
    val distance = remember(latitude, longitude) {
        val loc1 = Location("").apply {
            this.latitude = userLat
            this.longitude = userLon
        }
        val loc2 = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        loc1.distanceTo(loc2) / 1000.0 // in km
    }

    Column {
        Text(
            text = "Distance from you: ${String.format("%.1f", distance)} km",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
            cameraPositionState = cameraPositionState
        ) {
            GoogleMarker(
                state = MarkerState(position = location),
                title = title
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
