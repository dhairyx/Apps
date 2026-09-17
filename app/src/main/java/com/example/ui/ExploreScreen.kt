package com.example.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.EventWithOrganizer
import com.example.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: MainViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events = uiState.allEvents
    val context = LocalContext.current
    var itineraries by remember { mutableStateOf(emptyList<com.example.data.Itinerary>()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for BottomNavBar if needed
    ) {
        item {
            HeroSection()
        }
        item {
            SearchBar()
        }
        item {
            CategoriesSection()
        }
        item {
            MapSection(
                itineraries = itineraries,
                onAddItinerary = { name, point ->
                    val newItinerary = com.example.data.Itinerary(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        points = listOf(point)
                    )
                    itineraries = itineraries + newItinerary
                },
                onAddToExistingItinerary = { id, point ->
                    itineraries = itineraries.map {
                        if (it.id == id && !it.points.contains(point)) {
                            it.copy(points = it.points + point)
                        } else {
                            it
                        }
                    }
                }
            )
        }
        if (itineraries.isNotEmpty()) {
            item {
                Text(
                    text = "Saved Itineraries",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(itineraries) { itinerary ->
                        Card(
                            modifier = Modifier.width(200.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = itinerary.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${itinerary.points.size} locations",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Text(
                text = "Upcoming Trips near Toulouse",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )
        }
        items(events) { eventWithOrg ->
            EventCard(eventWithOrg, onClick = { navController.navigate("event_detail/${eventWithOrg.event.id}") })
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_toulouse_1785776202675),
            contentDescription = "Toulouse",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Explore Occitanie together",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Explore Occitanie together...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "JD",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun CategoriesSection() {
    val categories = listOf("Hiking", "History", "Wine", "Photo", "Cycling")
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    Column {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val suggestion = remember(selectedCategory) {
            when (selectedCategory) {
                "Hiking" -> "Pic Saint-Loup" to "A beautiful mountain for hiking with panoramic views."
                "History" -> "Cité de Carcassonne" to "A stunning medieval fortress with 3km of walls."
                "Wine" -> "Gaillac Vineyards" to "Explore historic vineyards and world-class local wine."
                "Photo" -> "Pont du Gard" to "An ancient Roman aqueduct bridge, perfect for photography."
                "Cycling" -> "Canal du Midi" to "Enjoy a relaxing bike ride along this historic canal under the shade of plane trees."
                else -> "Explore" to "Discover new places."
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "People's Choice",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "People's Choice: $selectedCategory",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = suggestion.first,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = suggestion.second,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(eventWithOrg: EventWithOrganizer, onClick: () -> Unit) {
    val context = LocalContext.current
    val event = eventWithOrg.event
    val imageResId = context.resources.getIdentifier(event.imageResName, "drawable", context.packageName)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.ic_launcher_background),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.location} • ${event.distanceKm} km from Toulouse",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.date,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Participants",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.currentParticipants}/${event.maxParticipants} people",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapSection(
    itineraries: List<com.example.data.Itinerary>,
    onAddItinerary: (String, com.example.data.TravelPoint) -> Unit,
    onAddToExistingItinerary: (String, com.example.data.TravelPoint) -> Unit
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var selectedPoint by remember { mutableStateOf<com.example.data.TravelPoint?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var activeRouteDestination by remember { mutableStateOf<com.example.data.TravelPoint?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var reviewsMap by remember { mutableStateOf(mapOf<String, List<String>>()) }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Ignore
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Explore the Map",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (locationPermissionsState.allPermissionsGranted) {
            MapContent(
                isMyLocationEnabled = true,
                userLocation = userLocation,
                activeRouteDestination = activeRouteDestination,
                onPointClick = { point ->
                    selectedPoint = point
                    showBottomSheet = true
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Enable location to see your position on the map.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showPermissionDialog = true }) {
                    Text("Enable Location")
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { 
                Text("To show your current position on the map, this app needs access to your fine and coarse location. Please grant the permissions in the next prompt.") 
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    locationPermissionsState.launchMultiplePermissionRequest()
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Not Now")
                }
            }
        )
    }

    if (showBottomSheet && selectedPoint != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            TravelPointDetail(
                point = selectedPoint!!,
                canRoute = userLocation != null,
                onPlanRoute = {
                    activeRouteDestination = selectedPoint
                    showBottomSheet = false
                },
                reviews = reviewsMap[selectedPoint!!.id] ?: emptyList(),
                onAddReview = { review ->
                    val currentList = reviewsMap[selectedPoint!!.id] ?: emptyList()
                    reviewsMap = reviewsMap + (selectedPoint!!.id to (currentList + review))
                },
                itineraries = itineraries,
                onAddItinerary = { name -> onAddItinerary(name, selectedPoint!!) },
                onAddToExistingItinerary = { id -> onAddToExistingItinerary(id, selectedPoint!!) }
            )
        }
    }
}

@Composable
fun TravelPointDetail(
    point: com.example.data.TravelPoint,
    canRoute: Boolean,
    onPlanRoute: () -> Unit,
    reviews: List<String>,
    onAddReview: (String) -> Unit,
    itineraries: List<com.example.data.Itinerary>,
    onAddItinerary: (String) -> Unit,
    onAddToExistingItinerary: (String) -> Unit
) {
    var showItineraryDialog by remember { mutableStateOf(false) }

    if (showItineraryDialog) {
        var newItineraryName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showItineraryDialog = false },
            title = { Text("Add to Itinerary") },
            text = {
                Column {
                    if (itineraries.isNotEmpty()) {
                        Text("Existing Itineraries:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        itineraries.forEach { itinerary ->
                            TextButton(onClick = {
                                onAddToExistingItinerary(itinerary.id)
                                showItineraryDialog = false
                            }) {
                                Text(itinerary.name)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text("Create New Itinerary:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newItineraryName,
                        onValueChange = { newItineraryName = it },
                        label = { Text("Itinerary Name") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItineraryName.isNotBlank()) {
                            onAddItinerary(newItineraryName)
                            showItineraryDialog = false
                        }
                    },
                    enabled = newItineraryName.isNotBlank()
                ) {
                    Text("Create & Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showItineraryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val encodedTitle = java.net.URLEncoder.encode(point.title.replace(" ", ""), "UTF-8")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(5) { index ->
                AsyncImage(
                    model = "https://loremflickr.com/400/300/landmark,$encodedTitle?lock=$index",
                    contentDescription = "${point.title} image $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(280.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = point.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = point.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val travelTip = remember(point.id) {
            val tips = listOf(
                "Early morning (8 AM - 10 AM) is best to avoid the biggest crowds.",
                "Late afternoon (after 4 PM) offers fewer crowds and great lighting.",
                "Visit on weekdays before noon for the quietest experience.",
                "Sunset hours are popular, try arriving just after sunrise for a peaceful visit."
            )
            tips[point.id.hashCode().let { if (it < 0) -it else it } % tips.size]
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Travel Tip",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Travel Tip",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = travelTip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        WeatherWidget(latitude = point.location.latitude, longitude = point.location.longitude)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        var isFavorite by remember { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFavorite) "Saved" else "Save")
            }
            if (canRoute) {
                Button(
                    onClick = onPlanRoute,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Plan Route",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Plan Route")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showItineraryDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Itinerary",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to Itinerary")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (reviews.isEmpty()) {
            Text(
                text = "No reviews yet. Be the first to review!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            reviews.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = review,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        var newReviewText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = newReviewText,
            onValueChange = { newReviewText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Write a review") },
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (newReviewText.isNotBlank()) {
                    onAddReview(newReviewText)
                    newReviewText = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = newReviewText.isNotBlank()
        ) {
            Text("Submit Review")
        }
    }
}

@Composable
fun MapContent(
    isMyLocationEnabled: Boolean,
    userLocation: LatLng?,
    activeRouteDestination: com.example.data.TravelPoint?,
    onPointClick: (com.example.data.TravelPoint) -> Unit
) {
    val toulouse = LatLng(43.6047, 1.4442)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(toulouse, 8f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true)
        ) {
            com.example.data.occitanieTouristSpots.forEach { spot ->
                Marker(
                    state = MarkerState(position = spot.location),
                    title = spot.title,
                    snippet = spot.description,
                    onClick = {
                        onPointClick(spot)
                        true
                    }
                )
            }
            if (userLocation != null && activeRouteDestination != null) {
                Polyline(
                    points = listOf(userLocation, activeRouteDestination.location),
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )
            }
        }
    }
}

@Composable
fun WeatherWidget(latitude: Double, longitude: Double) {
    var weatherData by remember { mutableStateOf<com.example.data.WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(latitude, longitude) {
        isLoading = true
        try {
            val response = com.example.data.WeatherApi.retrofitService.getForecast(
                latitude = latitude,
                longitude = longitude
            )
            weatherData = response
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Weather",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Current Weather",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 2.dp
                    )
                } else if (weatherData?.current != null) {
                    val current = weatherData!!.current!!
                    val maxPrecip = weatherData!!.hourly?.precipitationProbability?.maxOrNull() ?: 0
                    
                    Text(
                        text = "${current.temperature}°C, ${current.windSpeed} km/h wind\nHumidity: ${current.humidity}%, Precip chance: $maxPrecip%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Text(
                        text = "Weather data unavailable.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
