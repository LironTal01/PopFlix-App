package com.example.popiflix.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data model for popular search items
 * Contains search query and search type information
 */
@Parcelize
data class PopularSearch(
    val query: String, // search query text
    val type: SearchType, // type of search (movie, actor, etc.)
    val emoji: String? = null // optional emoji for display
) : Parcelable

enum class SearchType {
    MOVIE, // search for movies
    ACTOR, // search for actors
    DIRECTOR, // search for directors
    GENRE // search for genres
}

object PopularSearchesData {
    val popularSearches = listOf(
        // Popular Movies
        PopularSearch("Barbie", SearchType.MOVIE, "🎀"), // popular movie searches
        PopularSearch("Oppenheimer", SearchType.MOVIE, "💥"),
        PopularSearch("Spider-Man", SearchType.MOVIE, "🕷️"),
        PopularSearch("Avatar", SearchType.MOVIE, "💙"),
        PopularSearch("Top Gun", SearchType.MOVIE, "✈️"),
        PopularSearch("Black Panther", SearchType.MOVIE, "🖤"),
        PopularSearch("Dune", SearchType.MOVIE, "🏜️"),
        PopularSearch("The Batman", SearchType.MOVIE, "🦇"),
        
        // Popular Actors
        PopularSearch("Tom Cruise", SearchType.ACTOR, "🎬"), // popular actor searches
        PopularSearch("Leonardo DiCaprio", SearchType.ACTOR, "🏆"),
        PopularSearch("Margot Robbie", SearchType.ACTOR, "✨"),
        PopularSearch("Ryan Gosling", SearchType.ACTOR, "🎭"),
        PopularSearch("Emma Stone", SearchType.ACTOR, "💎"),
        PopularSearch("Timothée Chalamet", SearchType.ACTOR, "🌟"),
        PopularSearch("Zendaya", SearchType.ACTOR, "👑"),
        PopularSearch("Chris Evans", SearchType.ACTOR, "🛡️"),
        
        // Popular Directors
        PopularSearch("Christopher Nolan", SearchType.DIRECTOR, "🎥"), // popular director searches
        PopularSearch("Quentin Tarantino", SearchType.DIRECTOR, "🎬"),
        PopularSearch("Martin Scorsese", SearchType.DIRECTOR, "🎭"),
        PopularSearch("Denis Villeneuve", SearchType.DIRECTOR, "🌌"),
        PopularSearch("Greta Gerwig", SearchType.DIRECTOR, "✨"),
        PopularSearch("Jordan Peele", SearchType.DIRECTOR, "🎪"),
        PopularSearch("Ari Aster", SearchType.DIRECTOR, "🎭"),
        PopularSearch("Bong Joon-ho", SearchType.DIRECTOR, "🎨"),
        
        // Popular Genres
        PopularSearch("Action", SearchType.GENRE, "💥"), // popular genre searches
        PopularSearch("Comedy", SearchType.GENRE, "😂"),
        PopularSearch("Horror", SearchType.GENRE, "👻"),
        PopularSearch("Romance", SearchType.GENRE, "💕"),
        PopularSearch("Sci-Fi", SearchType.GENRE, "🚀"),
        PopularSearch("Thriller", SearchType.GENRE, "🔪"),
        PopularSearch("Drama", SearchType.GENRE, "🎭"),
        PopularSearch("Animation", SearchType.GENRE, "🎨")
    )
}
