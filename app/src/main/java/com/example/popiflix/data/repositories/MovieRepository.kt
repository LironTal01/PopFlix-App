package com.example.popiflix.data.repositories

import com.example.popiflix.data.models.MovieDetails
import com.example.popiflix.data.models.MovieResponse
import com.example.popiflix.data.models.GenreResponse
import com.example.popiflix.data.models.CreditsResponse
import com.example.popiflix.data.api.TmdbApi
import com.example.popiflix.data.models.Movie
import com.example.popiflix.data.models.RatingRequest
import com.example.popiflix.data.models.RatingResponse
import com.example.popiflix.data.models.GuestSession
import com.example.popiflix.data.models.GuestSessionResponse
import com.example.popiflix.data.models.AccountStatesResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for movie data operations
 * Handles API calls to TMDB and provides clean interface for ViewModels
 */
@Singleton
class MovieRepository @Inject constructor(
    private val api: TmdbApi
) {
    
    // TMDB Genre IDs
    companion object {
        const val ACTION_GENRE_ID = 28
        const val ADVENTURE_GENRE_ID = 12
        const val COMEDY_GENRE_ID = 35
        const val DRAMA_GENRE_ID = 18
        const val ROMANCE_GENRE_ID = 10749
        const val ANIMATION_GENRE_ID = 16
        const val SCIENCE_FICTION_GENRE_ID = 878
        const val HORROR_GENRE_ID = 27
        const val THRILLER_GENRE_ID = 53
        const val DOCUMENTARY_GENRE_ID = 99
    }

    // Get popular movies from TMDB API
    suspend fun getPopularMovies(apiKey: String, page: Int = 1, language: String? = null): MovieResponse {
        val response = api.getPopularMovies(apiKey, page, language) // call TMDB API
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load popular movies: ${response.message()}") // throw error if failed
    }

    // Get top rated movies from TMDB API (for Israeli audience)
    suspend fun getTopRatedMovies(apiKey: String, page: Int = 1): MovieResponse {
        val response = api.getTopRatedMovies(apiKey, page) // call TMDB API for top rated movies
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load top rated movies: ${response.message()}") // throw error if failed
    }

    // Get now playing movies from TMDB API (new in cinemas)
    suspend fun getNowPlayingMovies(apiKey: String, page: Int = 1): MovieResponse {
        val response = api.getNowPlayingMovies(apiKey, page) // call TMDB API
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load now playing movies: ${response.message()}") // throw error if failed
    }

    // Get upcoming movies from TMDB API
    suspend fun getUpcomingMovies(apiKey: String, page: Int = 1): MovieResponse {
        val response = api.getUpcomingMovies(apiKey, page) // call TMDB API
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load upcoming movies: ${response.message()}") // throw error if failed
    }

    // Get movies by genre using discover API
    suspend fun getMoviesByGenre(apiKey: String, genreId: Int, page: Int = 1): MovieResponse {
        val response = api.discoverMovies(
            apiKey = apiKey,
            genres = genreId.toString(),
            page = page
        ) // call TMDB discover API with genre filter
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load movies by genre: ${response.message()}") // throw error if failed
    }

    // Genre-specific functions
    suspend fun getActionAdventureMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, ACTION_GENRE_ID, page)
    }

    suspend fun getComedyMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, COMEDY_GENRE_ID, page)
    }

    suspend fun getDramaMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, DRAMA_GENRE_ID, page)
    }

    suspend fun getRomanceMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, ROMANCE_GENRE_ID, page)
    }

    suspend fun getAnimationMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, ANIMATION_GENRE_ID, page)
    }

    suspend fun getScienceFictionMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, SCIENCE_FICTION_GENRE_ID, page)
    }

    suspend fun getHorrorMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, HORROR_GENRE_ID, page)
    }

    suspend fun getThrillerMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, THRILLER_GENRE_ID, page)
    }

    suspend fun getDocumentaryMovies(apiKey: String, page: Int = 1): MovieResponse {
        return getMoviesByGenre(apiKey, DOCUMENTARY_GENRE_ID, page)
    }

    // Get detailed information for a specific movie
    suspend fun getMovieDetails(movieId: Int, apiKey: String): MovieDetails {
        val response = api.getMovieDetails(movieId, apiKey) // call TMDB API for movie details
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load movie details: ${response.message()}") // throw error if failed
    }

    // Get list of movie genres from TMDB
    suspend fun getGenres(apiKey: String): GenreResponse {
        val response = api.getGenres(apiKey) // call TMDB API for genres
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load genres: ${response.message()}") // throw error if failed
    }

    // Get cast and crew information for a movie
    suspend fun getMovieCredits(movieId: Int, apiKey: String): CreditsResponse {
        val response = api.getMovieCredits(movieId, apiKey) // call TMDB API for cast and crew
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to load movie credits: ${response.message()}") // throw error if failed
    }
    
    
    // Search for movies by title
    suspend fun searchMovies(query: String, apiKey: String, page: Int = 1): MovieResponse {
        val response = api.searchMovies(apiKey, query, page) // call TMDB API for movie search
        if (response.isSuccessful) return response.body()!! // return data if successful
        else throw Exception("Failed to search movies: ${response.message()}") // throw error if failed
    }
    

    // Enhanced search combining movies, actors, and directors with smart prioritization
    suspend fun searchCombined(query: String, apiKey: String, page: Int = 1): Pair<List<Movie>, Int> {
        // 1) Search by movie title (highest priority)
        val byTitleResp = api.searchMovies(apiKey, query, page) // search movies by title
        if (!byTitleResp.isSuccessful) throw Exception("Failed to search movies: ${byTitleResp.message()}")
        val byTitle = byTitleResp.body()!!.results // get movie results
        val titlePages = byTitleResp.body()!!.totalPages // get total pages

        // 2) Search people (actors and directors) by name - search multiple pages for better results
        val allPeople = mutableListOf<com.example.popiflix.data.models.Person>()
        try {
            // Search first 3 pages of people to get more comprehensive results
            for (page in 1..3) {
                val peopleResp = api.searchPeople(apiKey, query, page) // search people by name
                if (peopleResp.isSuccessful) {
                    val people = peopleResp.body()!!.results // get people results
                    allPeople.addAll(people) // add to list
                    // If we got less than 20 results, we've reached the end
                    if (people.size < 20) break // stop if no more results
                } else break // stop if API call failed
            }
        } catch (e: Exception) {
            // If people search fails, continue with empty list
        }

        // 3) Separate actors and directors (increase limit for better results)
        val actorIds = allPeople.map { it.id }.distinct().take(15) // get unique actor IDs
        val directorIds = allPeople.map { it.id }.distinct().take(15) // get unique director IDs

        // 4) Discover by cast (actors) - sorted by rating with minimum votes
        var discoverPages = 1
        val byActors = if (actorIds.isNotEmpty()) {
            val ids = actorIds.joinToString(",") // join IDs with commas
            val discResp = api.discoverMovies(
                apiKey = apiKey,
                withCast = ids, // search movies with these actors
                sortBy = "vote_average.desc", // sort by rating descending
                minVotes = 20, // minimum vote count
                page = page
            )
            if (discResp.isSuccessful) {
                discoverPages = discResp.body()!!.totalPages // update total pages
                discResp.body()!!.results // return movie results
            } else emptyList() // return empty list if failed
        } else emptyList() // return empty list if no actors

        // 5) Discover by crew (directors) - sorted by rating with minimum votes
        val byDirectors = if (directorIds.isNotEmpty()) {
            val ids = directorIds.joinToString(",") // join IDs with commas
            val discResp = api.discoverMovies(
                apiKey = apiKey,
                withCrew = ids, // search movies with these directors
                sortBy = "vote_average.desc", // sort by rating descending
                minVotes = 20, // minimum vote count
                page = page
            )
            if (discResp.isSuccessful) {
                discoverPages = maxOf(discoverPages, discResp.body()!!.totalPages) // update total pages
                discResp.body()!!.results // return movie results
            } else emptyList() // return empty list if failed
        } else emptyList() // return empty list if no directors

        // 6) Smart merge with prioritization:
        // - Title matches get highest priority
        // - Then by rating (descending)
        // - Filter out items without poster
        // - Remove duplicates
        val allResults = byTitle + byActors + byDirectors // combine all results
        val merged = allResults
            .filter { it.posterPath?.isNotEmpty() == true } // filter out movies without poster
            .distinctBy { it.id } // remove duplicates by ID
            .sortedWith(compareByDescending<Movie> { movie ->
                // Prioritize title matches
                if (byTitle.contains(movie)) 1 else 0 // title matches get priority
            }.thenByDescending { it.voteAverage } // then by rating
            .thenByDescending { it.voteCount }) // then by vote count

        val totalPages = maxOf(titlePages, discoverPages) // get maximum pages
        return merged to totalPages // return merged results and total pages
    }

    // Rate a movie using guest session
    suspend fun rateMovie(movieId: Int, rating: Double, apiKey: String, guestSessionId: String): RatingResponse {
        val ratingRequest = RatingRequest(rating) // create rating request object
        val response = api.rateMovie(movieId, apiKey, guestSessionId, ratingRequest) // call TMDB API to rate movie
        if (response.isSuccessful) return response.body()!! // return response if successful
        else throw Exception("Failed to rate movie: ${response.message()}") // throw error if failed
    }

    // Delete a movie rating
    suspend fun deleteMovieRating(movieId: Int, apiKey: String, guestSessionId: String): RatingResponse {
        val response = api.deleteMovieRating(movieId, apiKey, guestSessionId) // call TMDB API to delete rating
        if (response.isSuccessful) return response.body()!! // return response if successful
        else throw Exception("Failed to delete movie rating: ${response.message()}") // throw error if failed
    }

    // Create guest session for rating movies
    suspend fun createGuestSession(apiKey: String): GuestSession {
        val response = api.createGuestSession(apiKey) // call TMDB API to create guest session
        if (response.isSuccessful) {
            val guestSessionResponse = response.body()!! // get response body
            return GuestSession(
                success = guestSessionResponse.success, // map success field
                guestSessionId = guestSessionResponse.guest_session_id, // map session ID
                expiresAt = guestSessionResponse.expires_at // map expiration time
            )
        } else {
            throw Exception("Failed to create guest session: ${response.message()}") // throw error if failed
        }
    }

    // Get current user's rating for a movie
    suspend fun getMovieRating(movieId: Int, apiKey: String, guestSessionId: String): AccountStatesResponse {
        val response = api.getMovieRating(movieId, apiKey, guestSessionId) // call TMDB API to get movie rating
        if (response.isSuccessful) return response.body()!! // return response if successful
        else throw Exception("Failed to get movie rating: ${response.message()}") // throw error if failed
    }

}
