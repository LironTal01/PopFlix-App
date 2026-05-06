package com.example.popiflix.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for watchlist movies database operations
 * Provides CRUD operations for watchlist movies
 */
@Dao
interface WatchlistMovieDao {

    @Query("SELECT * FROM watchlist_movies ORDER BY date_added DESC")
    fun getAllWatchlistMovies(): LiveData<List<WatchlistMovie>> // get all watchlist movies by date

    @Query("SELECT * FROM watchlist_movies WHERE is_watched = 0 ORDER BY priority DESC, date_added DESC")
    fun getUnwatchedMovies(): LiveData<List<WatchlistMovie>> // get unwatched movies by priority

    @Query("SELECT * FROM watchlist_movies WHERE is_watched = 1 ORDER BY date_watched DESC")
    fun getWatchedMovies(): LiveData<List<WatchlistMovie>> // get watched movies by date

    @Query("SELECT * FROM watchlist_movies WHERE priority = :priority ORDER BY date_added DESC")
    fun getMoviesByPriority(priority: Int): LiveData<List<WatchlistMovie>> // get movies by priority level

    @Query("SELECT * FROM watchlist_movies WHERE id = :movieId")
    suspend fun getWatchlistMovieById(movieId: Int): WatchlistMovie? // get specific movie by ID

    @Query("SELECT * FROM watchlist_movies WHERE title LIKE '%' || :query || '%'")
    suspend fun searchWatchlistMovies(query: String): List<WatchlistMovie> // search movies by title

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistMovie(movie: WatchlistMovie) // add single movie to watchlist


    @Update
    suspend fun updateWatchlistMovie(movie: WatchlistMovie) // update existing movie

    @Delete
    suspend fun deleteWatchlistMovie(movie: WatchlistMovie) // delete movie by object

    @Query("DELETE FROM watchlist_movies WHERE id = :movieId")
    suspend fun deleteWatchlistMovieById(movieId: Int) // delete movie by ID

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE id = :movieId)")
    suspend fun isInWatchlist(movieId: Int): Boolean // check if movie is in watchlist

    @Query("UPDATE watchlist_movies SET is_watched = :isWatched, date_watched = :dateWatched WHERE id = :movieId")
    suspend fun updateWatchedStatus(movieId: Int, isWatched: Boolean, dateWatched: Long? = null) // mark as watched/unwatched

    @Query("UPDATE watchlist_movies SET user_notes = :notes WHERE id = :movieId")
    suspend fun updateNotes(movieId: Int, notes: String?) // update user notes

    @Query("UPDATE watchlist_movies SET priority = :priority WHERE id = :movieId")
    suspend fun updatePriority(movieId: Int, priority: Int) // update movie priority

    @Query("UPDATE watchlist_movies SET reminder_date = :reminderDate WHERE id = :movieId")
    suspend fun updateReminderDate(movieId: Int, reminderDate: Long?) // update reminder date

    @Query("SELECT COUNT(*) FROM watchlist_movies WHERE is_watched = 0")
    suspend fun getUnwatchedCount(): Int // count unwatched movies

    @Query("SELECT COUNT(*) FROM watchlist_movies WHERE is_watched = 1")
    suspend fun getWatchedCount(): Int // count watched movies

    @Query("SELECT * FROM watchlist_movies WHERE reminder_date IS NOT NULL AND reminder_date <= :currentTime AND is_watched = 0")
    suspend fun getMoviesWithDueReminders(currentTime: Long): List<WatchlistMovie> // get movies with due reminders

    @Query("SELECT * FROM watchlist_movies")
    suspend fun getAllWatchlistMoviesSync(): List<WatchlistMovie> // get all movies synchronously
}
