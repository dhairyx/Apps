# Travel Explorer App

A modern Android application built with Kotlin and Jetpack Compose that helps users discover beautiful travel destinations, get curated local insights, and check real-time weather conditions to plan their trips effectively.

## ✨ Features

*   **Interactive Destination Discovery:** Explore various travel points with a clean, Material Design 3 interface.
*   **Dynamic Photo Galleries:** Scroll through location-specific photo galleries dynamically loaded over the network using Coil and placeholder image services.
*   **Real-time Weather Forecasts:** Check current weather conditions (temperature, wind speed, relative humidity, and precipitation probability) for any destination, powered by the Open-Meteo API.
*   **Curated Travel Tips:** Get smart recommendations on the best times of day to visit specific locations to avoid crowds and enjoy the best lighting.
*   **People's Choice Suggestions:** Filter destinations by preferences (Hiking, History, Wine, Photo, Cycling) and view dynamic "People's Choice" recommendations tailored to your interests.
*   **Modern UI/UX:** Built entirely with Jetpack Compose, featuring smooth bottom sheets, interactive chips, and polished Material 3 components.

## 🛠 Tech Stack

*   **Language:** [Kotlin](https://kotlinlang.org/)
*   **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Design System:** Material Design 3 (M3)
*   **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Moshi](https://github.com/square/moshi) (for Open-Meteo API integration)
*   **Image Loading:** [Coil](https://coil-kt.github.io/coil/) (Compose integration)
*   **Architecture:** Clean Architecture principles with state hoisted in standard Compose patterns.

## 🚀 Getting Started

To run this project locally on your machine:

1.  **Export the Project:** Download the project as a `.zip` file from your AI Studio workspace.
2.  **Open in Android Studio:** Extract the `.zip` and open the folder in [Android Studio](https://developer.android.com/studio) (Koala or newer recommended).
3.  **Sync Gradle:** Allow Android Studio to download the necessary dependencies via Gradle.
4.  **Run the App:** Connect an Android device or start an Android Emulator, then click the green **Run** button (`Shift + F10`).

## 📡 API Integrations

*   **[Open-Meteo](https://open-meteo.com/):** Used for fetching free, real-time weather data. No API keys are required to build and run this project.
