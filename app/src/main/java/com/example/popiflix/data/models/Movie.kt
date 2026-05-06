package com.example.popiflix.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Movie data model representing a movie from TMDB API
 * Contains basic movie information like title, overview, ratings, etc.
 */
@Parcelize
data class Movie(
    val id: Int,                    // Unique movie ID from TMDB
    val title: String,              // Movie title
    val overview: String,           // Movie description/summary
    @SerializedName("poster_path")
    val posterPath: String?,        // Poster image path (can be null)
    @SerializedName("backdrop_path")
    val backdropPath: String?,      // Backdrop image path (can be null)
    @SerializedName("release_date")
    val releaseDate: String,        // Release date string
    @SerializedName("vote_average")
    val voteAverage: Double,        // Average rating (0-10)
    @SerializedName("vote_count")
    val voteCount: Int,             // Number of votes
    val popularity: Double,         // Popularity score
    @SerializedName("genre_ids")
    val genreIds: List<Int>,        // List of genre IDs
    @SerializedName("original_language")
    val originalLanguage: String,   // Original language code
    @SerializedName("original_title")
    val originalTitle: String,      // Original title
    val adult: Boolean,             // Is adult content
    val video: Boolean              // Has video content
) : Parcelable {

    // Helper function to get full poster URL
    fun getFullPosterPath(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } // build full poster URL
    }

    // Helper function to get full backdrop URL
    fun getFullBackdropPath(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" } // build full backdrop URL
    }

    // Helper function to format rating
    fun getFormattedRating(): String {
        return String.format("%.1f", voteAverage) // format rating to 1 decimal place
    }

    // Helper function to get release year
    fun getReleaseYear(): String? {
        return if (releaseDate.isNotEmpty()) releaseDate.substring(0, 4) else null // extract year from date
    }
}