package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "current") val current: CurrentData? = null,
    @Json(name = "hourly") val hourly: HourlyData? = null
)

@JsonClass(generateAdapter = true)
data class CurrentData(
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "relative_humidity_2m") val humidity: Int,
    @Json(name = "wind_speed_10m") val windSpeed: Double
)

@JsonClass(generateAdapter = true)
data class HourlyData(
    @Json(name = "temperature_2m") val temperature2m: List<Double>,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>
)

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,precipitation_probability",
        @Query("forecast_days") forecastDays: Int = 1
    ): WeatherResponse
}

object WeatherApi {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    val retrofitService: WeatherService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(WeatherService::class.java)
    }
}
