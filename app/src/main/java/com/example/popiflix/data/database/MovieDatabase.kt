package com.example.popiflix.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for storing favorite and watchlist movies
 * Contains DAOs for data access operations
 */
@Database(
    entities = [FavoriteMovie::class, WatchlistMovie::class], // define database entities
    version = 5, // database version number
    exportSchema = false // disable schema export
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao // get favorites DAO
    abstract fun watchlistMovieDao(): WatchlistMovieDao // get watchlist DAO
}
