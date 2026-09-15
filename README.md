<div align="center">

# 🎬 PopFlix

### Android movie discovery app

Browse movies, search by title or person, save what you want to watch, and keep your preferences on device.

<p>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Architecture-MVVM-0A66C2" alt="MVVM">
  <img src="https://img.shields.io/badge/Data-TMDB-01B4E4" alt="TMDB">
</p>

</div>

## About

PopFlix is a native Android application built with Kotlin. It uses the TMDB API to surface popular, now-playing, top-rated, and upcoming movies, then lets users search, explore details, create a watchlist, save favorites, and submit guest ratings.

The project was developed as a final Android course project. Its focus is a responsive mobile experience with a clear separation between UI, state, local persistence, and remote data.

## What it supports

- Browse movie collections, genre-based results, and detailed cast and crew information.
- Search movies and people, with a persistent history of recent searches.
- Save favorites and watchlist entries locally.
- Rate movies through a TMDB guest session.
- Switch between light, dark, and system themes.
- Use the app in English or Hebrew, including right-to-left layouts.
- Refresh key movie collections with WorkManager when network and battery constraints allow.

## Engineering highlights

- **MVVM + Repository pattern** keeps fragments, ViewModels, repositories, and data sources separate.
- **Hilt** provides the app's database, networking, and background-work dependencies.
- **Retrofit, OkHttp, and Gson** handle typed TMDB requests, automatic API-key injection, language-aware requests, and JSON parsing.
- **Room** persists favorites and watchlist entries; **SharedPreferences** persists search history and app settings.
- **Navigation Component** provides fragment navigation with type-safe arguments; **View Binding** keeps UI references safer and more concise.
- **Coroutines** handle asynchronous work, while **WorkManager** schedules a unique daily refresh near 9:00 AM.

## Architecture

```text
Fragments and adapters
        ↓
ViewModels
        ↓
Repositories
   ↙          ↘
TMDB API      On-device storage
Retrofit      Room + SharedPreferences
```

The networking layer adds the TMDB API key and the selected app language to requests. Hilt supplies the API client, repositories, database, and WorkManager integration.

## Technology

**Platform and architecture:** Kotlin, Android SDK, MVVM, Repository pattern, View Binding

**Networking:** TMDB API, Retrofit, OkHttp, Gson

**Data and app services:** Room, SharedPreferences, Hilt, Coroutines, WorkManager

**UI:** Fragments, RecyclerView, Navigation Component, Material Components, Glide

## Run locally

### Prerequisites

- Android Studio
- An Android device or emulator running Android 8.1 (API 27) or later
- A TMDB API key

### Setup

1. Clone the repository and open it in Android Studio.
2. Create or update `local.properties` in the project root:

   ```properties
   TMDB_API_KEY=your_tmdb_api_key
   ```

3. Sync the Gradle project and run the `app` configuration on a device or emulator.

`local.properties` is ignored by Git, so the API key stays local. The build reads it into `BuildConfig`; do not add the key to source files.

## Screens

<div align="center">

<table>
  <tr>
    <td align="center">
      <strong>Home</strong><br><br>
      <img src="popflix_readme/home-screen.jpg" alt="PopFlix home screen" width="210">
    </td>
    <td align="center">
      <strong>Search</strong><br><br>
      <img src="popflix_readme/search-screen.jpg" alt="Movie search screen" width="210">
    </td>
    <td align="center">
      <strong>Movie details</strong><br><br>
      <img src="popflix_readme/movie-details-screen.jpg" alt="Movie details screen" width="210">
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Favorites</strong><br><br>
      <img src="popflix_readme/favorites-screen.jpg" alt="Favorites screen" width="210">
    </td>
    <td align="center">
      <strong>Watchlist</strong><br><br>
      <img src="popflix_readme/watchlist-screen.jpg" alt="Watchlist screen" width="210">
    </td>
    <td align="center">
      <strong>Settings</strong><br><br>
      <img src="popflix_readme/settings-screen.jpg" alt="Language and theme settings" width="210">
    </td>
  </tr>
</table>

</div>

## Project structure

```text
app/src/main/java/com/example/popiflix/
├── data/          # TMDB API, Room entities/DAOs, repositories, dependency injection
├── ui/            # Activity, fragments, ViewModels, and RecyclerView adapters
├── util/          # Language and shared application utilities
└── workers/       # WorkManager scheduling and refresh workers
```

## Attribution

Movie data and artwork are provided by [TMDB](https://www.themoviedb.org/).

## Author

Liron Tal  
B.Sc. Computer Science, Reichman University
