package com.example.popiflix.data.models

import com.google.gson.annotations.SerializedName

/**
 * Request model for movie rating submission to TMDB API
 * Contains the rating value to be submitted
 */
data class RatingRequest(
    @SerializedName("value")
    val value: Double // rating value (0.5 to 10.0)
)

/**
 * Response model for rating operations
 */
data class RatingResponse(
    @SerializedName("status_code")
    val statusCode: Int, // HTTP status code
    @SerializedName("status_message")
    val statusMessage: String, // status message
    @SerializedName("success")
    val success: Boolean // operation success status
)
