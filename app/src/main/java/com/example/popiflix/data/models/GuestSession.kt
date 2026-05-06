package com.example.popiflix.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GuestSession(
    val success: Boolean, // session creation success
    val guestSessionId: String, // guest session ID
    val expiresAt: String // session expiration time
) : Parcelable

/**
 * Response model for guest session creation from TMDB API
 * Contains guest session ID and expiration time
 */
@Parcelize
data class GuestSessionResponse(
    val success: Boolean, // session creation success
    val guest_session_id: String, // guest session ID from API
    val expires_at: String // session expiration time from API
) : Parcelable
