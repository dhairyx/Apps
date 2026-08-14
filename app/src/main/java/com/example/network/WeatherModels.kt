package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResult>?
)

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "name") val name: String,
    @Json(name = "country") val country: String?
)

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "daily") val daily: DailyWeather?
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "time") val time: List<String>,
    @Json(name = "weathercode") val weatherCode: List<Int>,
    @Json(name = "temperature_2m_max") val maxTemp: List<Double>,
    @Json(name = "temperature_2m_min") val minTemp: List<Double>
)
