package com.example.popiflix.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: Int, // unique genre ID
    val name: String // genre name
) : Parcelable

/**
 * Response model for movie genres from TMDB API
 * Contains list of available movie genres
 */
data class GenreResponse(
    val genres: List<Genre> // list of available genres
)
