# Occitanie Travel

A native Android app for discovering and joining group travel excursions around **Toulouse and the Occitanie region** of France. Built with Kotlin and Jetpack Compose, it was generated as a Google AI Studio app project.

## Features

- **Explore** – Browse a feed of local events and excursions, each with an organizer, location, and details. Includes search and a hero section for featured trips.
- **Map** – View events geographically using Google Maps.
- **Groups** – See and manage travel groups.
- **My Trips** – Track events you've joined.
- **Profile** – User profile screen.
- Local persistence of events/organizers via a Room database, seeded with initial sample data on first launch.
- Weather lookups via a Retrofit/Moshi-based network layer (`WeatherApi`, `WeatherNetwork`).

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3, Navigation Compose
- **Architecture:** MVVM (`MainViewModel` + `StateFlow`) with an `AndroidViewModel`
- **Persistence:** Room (`AppDatabase`, `EventDao`, `EventEntity`, `OrganizerEntity`)
- **Networking:** Retrofit, OkHttp, Moshi (for weather data)
- **Maps/Location:** Google Maps Compose, Play Services Maps & Location, osmdroid, Accompanist Permissions
- **Other:** Coil (image loading), Firebase (AI/App Check/Firestore-ready), KSP for annotation processing
- **Testing:** JUnit, Robolectric, Roborazzi (screenshot tests), Espresso, Compose UI Test

## Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt              # App entry point
├── data/                        # Room database, DAOs, entities, repository
│   ├── AppDatabase.kt
│   ├── EventDao.kt
│   ├── EventEntity.kt
│   ├── EventWithOrganizer.kt
│   ├── EventRepository.kt
│   └── OrganizerEntity.kt
├── network/                     # Weather API client
│   ├── WeatherApi.kt
│   ├── WeatherModels.kt
│   └── WeatherNetwork.kt
└── ui/                           # Compose screens, navigation, theme
    ├── AppNavigation.kt
    ├── ExploreScreen.kt
    ├── EventDetailScreen.kt
    ├── ProfileScreen.kt
    ├── TripsScreen.kt
    ├── MainViewModel.kt
    └── theme/
```

## Requirements

- Android Studio (latest stable)
- JDK 11+
- Android SDK: minSdk 24, targetSdk/compileSdk 36

## Setup

1. **Clone/extract** this project and open it in Android Studio.
2. **API keys:** Copy `.env.example` to `.env` and fill in the required values:
   - `MAPS_API_KEY` – Google Maps API key (a placeholder is currently hardcoded in `app/build.gradle.kts`; replace it with your own key for production use).
   - `GEMINI_API_KEY` – only needed if you enable Gemini API calls (uncomment the relevant line in `.env.example`).
3. **Sync Gradle** and let Android Studio download dependencies.
4. **Run** the app on an emulator or device (`minSdk 24`+).

> Note: For release builds, a signing config expects a keystore at `my-upload-key.jks` (or a path from the `KEYSTORE_PATH` env var) along with `STORE_PASSWORD` and `KEY_PASSWORD` environment variables. Debug builds use the bundled `debug.keystore`.

## Permissions

The app requests:
- `INTERNET`
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`

## Building

```bash
./gradlew assembleDebug     # Debug build
./gradlew assembleRelease   # Release build (requires signing config)
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

## License

No license file is included in this project. Add one if you intend to distribute or open-source this app.
