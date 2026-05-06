package com.example.popiflix.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Response model for account states from TMDB API
 * Contains user's rating and watchlist status for a movie
 */
@Parcelize
data class AccountStatesResponse(
    val id: Int, // movie ID
    val favorite: Boolean, // is movie in favorites
    val rated: Boolean, // has user rated the movie
    val watchlist: Boolean, // is movie in watchlist
    val rating: Double? // user's rating value
) : Parcelable
