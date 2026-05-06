package com.example.popiflix.data.models

/**
 * Response model for person search results from TMDB API
 * Contains list of person search results
 */
data class PersonSearchResponse(
    val page: Int, // current page number
    val results: List<Person>, // list of person search results
    val totalPages: Int, // total number of pages
    val totalResults: Int // total number of results
)

data class Person(
    val id: Int, // person ID
    val name: String? // person name
)
