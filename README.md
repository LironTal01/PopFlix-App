# PopFlix

PopFlix is an Android movie discovery app built with **Kotlin** and the **TMDB API**. It helps users discover movies, search with history, save favorites, manage a personal watchlist, rate movies, and use the app in both **English** and **Hebrew**.

## Project Overview

This project was developed as a final Android course project and demonstrates modern Android development practices, including **MVVM architecture**, **Repository pattern**, **Room database**, **Retrofit**, **Hilt**, **Coroutines**, and **WorkManager**.

## Key Features

- Browse **popular**, **new**, and **upcoming** movies
- Search for movies with **search history**
- View detailed movie information
- Save movies to **Favorites**
- Manage a personal **Watchlist**
- Store local data with **Room Database**
- Support **Dark / Light mode**
- Support **English / Hebrew** and RTL layout for Hebrew
- Rate movies through the app
- Background data refresh with **WorkManager**

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM, Repository Pattern
- **UI:** Android Fragments, RecyclerView, Material Design
- **Networking:** Retrofit, Gson, OkHttp
- **Dependency Injection:** Hilt
- **Local Storage:** Room Database
- **Async Programming:** Coroutines
- **Background Tasks:** WorkManager
- **Image Loading:** Glide
- **Navigation:** Navigation Component
- **API:** TMDB API

## App Screens

### Home Screen
The home screen presents featured and categorized movie content in a clean, modern layout.

![Home Screen](home-screen.jpg)

### Search
Users can search for movies and benefit from recent search history for a smoother experience.

![Search Screen](search-screen.jpg)

### Favorites
Users can keep track of movies they want quick access to in a dedicated favorites screen.

![Favorites Screen](favorites-screen.jpg)

### Movie Details
Each movie includes a dedicated details screen with core information and user actions.

![Movie Details Screen](movie-details-screen.jpg)

### Watchlist
The watchlist helps users organize movies they plan to watch.

![Watchlist Screen](watchlist-screen.jpg)

### Settings
The settings screen supports theme and language preferences.

![Settings Screen](settings-screen.jpg)

## Architecture

PopFlix follows the **MVVM** pattern:

- **UI Layer:** Fragments and adapters
- **ViewModel Layer:** Manages UI state and screen logic
- **Repository Layer:** Connects the UI to remote and local data sources
- **Remote Data Source:** TMDB API via Retrofit
- **Local Data Source:** Room database for favorites, watchlist, and search history

### Main Flow
`UI -> ViewModel -> Repository -> API / Database`

## Data Layer

The project uses Room to manage local persistence for:

- Favorite movies
- Watchlist movies
- Search history
- User-related local preferences and stored movie data

## Notable Technical Highlights

- Multi-language support with **dynamic language switching**
- **RTL support** for Hebrew
- Local persistence for offline-friendly user data
- Background refresh for core content using **WorkManager**
- Reusable adapters and fragment-based navigation
- Modern Android app structure with clear separation of concerns

## Libraries Used

- Retrofit
- Gson
- OkHttp
- Material Design Components
- Navigation Component
- Glide
- Hilt
- Room
- ViewModel & LiveData
- Coroutines
- WorkManager

## Purpose

The goal of PopFlix is to provide a modern, user-friendly platform for discovering, organizing, and tracking movies on Android.

## Author

**Liron Tal**  
B.Sc. Computer Science Student, Reichman University
