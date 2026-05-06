package com.example.popiflix.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for favorite movies stored in Room database
 * Contains movie data with localized titles
 */
@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey
    val id: Int, // unique movie ID from TMDB
    val title: String, // original movie title
    @ColumnInfo(name = "title_en")
    val titleEn: String? = null, // English title
    @ColumnInfo(name = "title_he")
    val titleHe: String? = null, // Hebrew title
    val overview: String, // movie description
    @ColumnInfo(name = "poster_path")
    val posterPath: String?, // poster image path
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?, // backdrop image path
    @ColumnInfo(name = "release_date")
    val releaseDate: String, // movie release date
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double, // average rating from TMDB
    @ColumnInfo(name = "vote_count")
    val voteCount: Int, // number of votes
    val popularity: Double, // movie popularity score
    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis(), // when added to favorites
    @ColumnInfo(name = "user_rating")
    val userRating: Int? = null, // user's personal rating (1-5 stars)
    @ColumnInfo(name = "user_review")
    val userReview: String? = null, // user's personal review
    @ColumnInfo(name = "genres")
    val genres: String? = null,  // genres as comma-separated string
    @ColumnInfo(name = "genres_en")
    val genresEn: String? = null, // English genres
    @ColumnInfo(name = "genres_he")
    val genresHe: String? = null  // Hebrew genres
) {
    /**
     * Get the appropriate title based on system language
     */
    fun getLocalizedTitle(): String {
        return when (java.util.Locale.getDefault().language) {
            "iw", "he" -> titleHe ?: titleEn ?: title // return Hebrew title if available
            else -> titleEn ?: title // return English title if available
        }
    }

    /**
     * Get the appropriate genres based on system language
     */
    fun getLocalizedGenres(): String {
        return when (java.util.Locale.getDefault().language) {
            "iw", "he" -> genresHe ?: genresEn ?: genres ?: "" // return Hebrew genres if available
            else -> genresEn ?: genres ?: "" // return English genres if available
        }
    }
}
