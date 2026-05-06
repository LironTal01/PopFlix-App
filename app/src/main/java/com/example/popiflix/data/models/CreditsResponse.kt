package com.example.popiflix.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Response model for movie credits from TMDB API
 * Contains cast and crew information for a movie
 */
@Parcelize
data class CreditsResponse(
    val id: Int, // movie ID
    val cast: List<CastMember>, // list of cast members
    val crew: List<CrewMember> // list of crew members
) : Parcelable

@Parcelize
data class CastMember(
    val id: Int, // person ID
    val name: String, // actor name
    @SerializedName("character")
    val character: String, // character name
    @SerializedName("profile_path")
    val profilePath: String?, // profile image path
    @SerializedName("order")
    val order: Int // cast order (billing)
) : Parcelable

@Parcelize
data class CrewMember(
    val id: Int, // person ID
    val name: String, // crew member name
    @SerializedName("job")
    val job: String, // job title
    @SerializedName("department")
    val department: String, // department (Directing, Writing, etc.)
    @SerializedName("profile_path")
    val profilePath: String? // profile image path
) : Parcelable
