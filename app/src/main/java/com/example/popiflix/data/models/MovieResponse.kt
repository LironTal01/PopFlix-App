package com.example.popiflix.data.models

import com.google.gson.annotations.SerializedName

/**
 * Response model for movie list from TMDB API
 * Contains list of movies with pagination information
 */
data class MovieResponse(
    val page: Int, // current page number
    val results: List<Movie>, // list of movies
    @SerializedName("total_pages")
    val totalPages: Int, // total number of pages
    @SerializedName("total_results")
    val totalResults: Int // total number of results
) {
    fun hasMorePages(): Boolean = page < totalPages // check if there are more pages
}



