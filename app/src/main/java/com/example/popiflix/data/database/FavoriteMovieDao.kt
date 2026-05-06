package com.example.popiflix.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for favorite movies database operations
 * Provides CRUD operations for favorite movies
 */
@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movies ORDER BY date_added DESC")
    fun getAllFavorites(): LiveData<List<FavoriteMovie>> // get all favorites by date added

    @Query("SELECT * FROM favorite_movies ORDER BY title ASC")
    fun getAllFavoritesSortedByTitle(): LiveData<List<FavoriteMovie>> // get favorites sorted by title

    @Query("SELECT * FROM favorite_movies ORDER BY vote_average DESC")
    fun getAllFavoritesSortedByRating(): LiveData<List<FavoriteMovie>> // get favorites sorted by rating

    @Query("SELECT * FROM favorite_movies ORDER BY release_date DESC")
    fun getAllFavoritesSortedByDate(): LiveData<List<FavoriteMovie>> // get favorites sorted by release date

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovie? // get specific favorite by ID

    @Query("SELECT * FROM favorite_movies WHERE title LIKE '%' || :query || '%'")
    suspend fun searchFavorites(query: String): List<FavoriteMovie> // search favorites by title

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovie) // add single favorite

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(movies: List<FavoriteMovie>) // add multiple favorites

    @Update
    suspend fun updateFavorite(movie: FavoriteMovie) // update existing favorite

    @Delete
    suspend fun deleteFavorite(movie: FavoriteMovie) // delete favorite by object

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteById(movieId: Int) // delete favorite by ID


    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean // check if movie is favorite

    @Query("SELECT COUNT(*) FROM favorite_movies")
    suspend fun getFavoritesCount(): Int // get total favorites count

    
    @Query("SELECT * FROM favorite_movies")
    suspend fun getAllFavoritesSync(): List<FavoriteMovie> // get all favorites synchronously
}
