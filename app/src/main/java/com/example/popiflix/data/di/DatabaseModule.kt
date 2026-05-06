package com.example.popiflix.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.popiflix.data.database.MovieDatabase
import com.example.popiflix.data.database.FavoriteMovieDao
import com.example.popiflix.data.database.WatchlistMovieDao
import com.example.popiflix.data.repositories.SearchHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database module for dependency injection
 * Provides Room database, DAOs, and other database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(
            context, // application context
            MovieDatabase::class.java, // database class
            "movies.db" // database name
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5) // add database migrations
            .build() // build database instance

    // Migration 1→2: Add separate English and Hebrew title columns for localization
    private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
        override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
            // Add new columns to watchlist_movies table
            database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN title_en TEXT") // add English title column
            database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN title_he TEXT") // add Hebrew title column
            
            // Add new columns to favorite_movies table
            database.execSQL("ALTER TABLE favorite_movies ADD COLUMN title_en TEXT") // add English title column
            database.execSQL("ALTER TABLE favorite_movies ADD COLUMN title_he TEXT") // add Hebrew title column
            
            // Copy existing title to both title_en and title_he for existing records
            database.execSQL("UPDATE watchlist_movies SET title_en = title, title_he = title WHERE title_en IS NULL") // copy titles for watchlist
            database.execSQL("UPDATE favorite_movies SET title_en = title, title_he = title WHERE title_en IS NULL") // copy titles for favorites
        }
    }

    // Migration 2→3: Add genres columns (English and Hebrew) to favorites table
    private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
        override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
            // Add genres columns to favorite_movies table
            database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres TEXT") // add original genres column
            database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres_en TEXT") // add English genres column
            database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres_he TEXT") // add Hebrew genres column
        }
    }

    // Migration 3→4: Add genres columns (English and Hebrew) to watchlist table
    private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
        override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
            // Add genres columns to watchlist_movies table
            database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres TEXT") // add original genres column
            database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres_en TEXT") // add English genres column
            database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres_he TEXT") // add Hebrew genres column
        }
    }

    // Migration 4→5: Add genres columns (English and Hebrew) to both tables for existing users
    private val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
        override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
            // Add genres columns to favorite_movies table (if not already present)
            try {
                database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres TEXT") // add original genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
            try {
                database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres_en TEXT") // add English genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
            try {
                database.execSQL("ALTER TABLE favorite_movies ADD COLUMN genres_he TEXT") // add Hebrew genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
            
            // Add genres columns to watchlist_movies table (if not already present)
            try {
                database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres TEXT") // add original genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
            try {
                database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres_en TEXT") // add English genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
            try {
                database.execSQL("ALTER TABLE watchlist_movies ADD COLUMN genres_he TEXT") // add Hebrew genres column
            } catch (e: Exception) {
                // Column might already exist, ignore error
            }
        }
    }


    @Provides
    fun provideFavoriteMovieDao(database: MovieDatabase): FavoriteMovieDao =
        database.favoriteMovieDao() // provide favorites DAO
    @Provides
    fun provideWatchlistMovieDao(database: MovieDatabase): WatchlistMovieDao =
        database.watchlistMovieDao() // provide watchlist DAO

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(@ApplicationContext context: Context): SearchHistoryRepository =
        SearchHistoryRepository(context) // provide search history repository

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("popiflix_prefs", Context.MODE_PRIVATE) // provide app preferences


}
