package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.EventEntity
import com.example.data.EventWithOrganizer
import com.example.data.EventRepository
import com.example.network.DailyWeather
import com.example.network.WeatherNetwork
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val allEvents: List<EventWithOrganizer> = emptyList(),
    val joinedEvents: List<EventWithOrganizer> = emptyList(),
    val isLoading: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = EventRepository(database.eventDao())

    val uiState: StateFlow<MainUiState> = combine(
        repository.allEvents,
        repository.joinedEvents
    ) { allEvents, joinedEvents ->
        MainUiState(
            allEvents = allEvents,
            joinedEvents = joinedEvents,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            val currentEvents = repository.allEvents.first()
            if (currentEvents.isEmpty()) {
                repository.insertInitialData()
            }
        }
    }

    fun joinEvent(id: Int) {
        viewModelScope.launch {
            repository.joinEvent(id)
        }
    }
    
    fun leaveEvent(id: Int) {
        viewModelScope.launch {
            repository.leaveEvent(id)
        }
    }
    
    fun getEventById(id: Int): StateFlow<EventWithOrganizer?> {
        return repository.getEventById(id).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    private val _weatherState = MutableStateFlow<DailyWeather?>(null)
    val weatherState: StateFlow<DailyWeather?> = _weatherState.asStateFlow()

    private val _locationCoordinates = MutableStateFlow<Pair<Double, Double>?>(null)
    val locationCoordinates: StateFlow<Pair<Double, Double>?> = _locationCoordinates.asStateFlow()

    fun fetchWeather(location: String) {
        viewModelScope.launch {
            try {
                // Clear previous state while fetching
                _weatherState.value = null
                _locationCoordinates.value = null
                
                val geoResponse = WeatherNetwork.geocodingApi.searchCity(name = location)
                val results = geoResponse.results
                if (!results.isNullOrEmpty()) {
                    val firstResult = results[0]
                    _locationCoordinates.value = Pair(firstResult.latitude, firstResult.longitude)
                    val weatherResponse = WeatherNetwork.weatherApi.getWeatherForecast(
                        latitude = firstResult.latitude,
                        longitude = firstResult.longitude
                    )
                    _weatherState.value = weatherResponse.daily
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
