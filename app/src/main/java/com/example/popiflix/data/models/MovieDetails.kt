package com.example.popiflix.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Detailed movie information from TMDB API
 * Contains comprehensive movie data including runtime, genres, production info
 */
@Parcelize
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val runtime: Int?,
    val budget: Long,
    val revenue: Long,
    val genres: List<Genre>,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,
    val tagline: String?,
    val homepage: String?,
    val status: String,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    val popularity: Double,
    val adult: Boolean,
    val video: Boolean,
    @SerializedName("imdb_id")
    val imdbId: String?,
) : Parcelable {

    fun getFullPosterPath(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } // build full poster URL
    }

    fun getFullBackdropPath(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" } // build full backdrop URL
    }

    fun getFormattedRating(): String {
        return String.format("%.1f", voteAverage) // format rating to 1 decimal place
    }

    fun getRuntimeFormatted(): String? {
        return runtime?.let {
            val hours = it / 60 // calculate hours
            val minutes = it % 60 // calculate remaining minutes
            "${hours}h ${minutes}m" // format as "2h 30m"
        }
    }

    fun getGenresString(): String {
        return genres.joinToString(", ") { it.name } // join genre names with commas
    }

    fun getBudgetFormatted(): String {
        return if (budget > 0) "$${String.format("%,d", budget)}" else "N/A" // format budget with commas
    }

    fun getRevenueFormatted(): String {
        return if (revenue > 0) "$${String.format("%,d", revenue)}" else "N/A" // format revenue with commas
    }
    
}

@Parcelize
data class ProductionCompany(
    val id: Int,
    val name: String,
    @SerializedName("logo_path")
    val logoPath: String?,
    @SerializedName("origin_country")
    val originCountry: String
) : Parcelable

@Parcelize
data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso31661: String,
    val name: String
) : Parcelable

@Parcelize
data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("iso_639_1")
    val iso6391: String,
    val name: String
) : Parcelable
