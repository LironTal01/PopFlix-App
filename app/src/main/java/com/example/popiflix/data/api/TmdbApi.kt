package com.example.popiflix.data.api

import com.example.popiflix.data.models.MovieDetails
import com.example.popiflix.data.models.MovieResponse
import com.example.popiflix.data.models.GenreResponse
import com.example.popiflix.data.models.CreditsResponse
import com.example.popiflix.data.models.PersonSearchResponse
import com.example.popiflix.data.models.RatingRequest
import com.example.popiflix.data.models.RatingResponse
import com.example.popiflix.data.models.GuestSessionResponse
import com.example.popiflix.data.models.AccountStatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body

/**
 * TMDB API interface for Retrofit
 * Contains all API endpoints for movie data, ratings, and user sessions
 */
interface TmdbApi {

    // get popular movies
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // get top rated movies
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // get movies now playing in cinemas
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // get upcoming movies
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // search movies by text
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("query") query: String, // search text entered by user
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null, // language code for localized results
        @Query("include_adult") includeAdult: Boolean = false // whether to include adult content
    ): Response<MovieResponse>
    


    // get full movie details by id
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int, // unique movie identifier
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieDetails>


    // get movie credits (cast and crew)
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int, // unique movie identifier
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("language") language: String? = null // language code for localized results
    ): Response<CreditsResponse>

    // discover movies with filters
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("sort_by") sortBy: String = "popularity.desc", // sorting criteria
        @Query("page") page: Int = 1, // page number for pagination
        @Query("with_genres") genres: String? = null, // comma-separated genre IDs
        @Query("primary_release_year") year: Int? = null, // release year filter
        @Query("vote_average.gte") minRating: Double? = null, // minimum rating filter
        @Query("vote_average.lte") maxRating: Double? = null, // maximum rating filter
        @Query("with_runtime.gte") minRuntime: Int? = null, // minimum runtime in minutes
        @Query("with_runtime.lte") maxRuntime: Int? = null, // maximum runtime in minutes
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // get trending movies today
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // get list of genres
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("language") language: String? = null // language code for localized results
    ): Response<GenreResponse>
    

    @GET("search/person")
    suspend fun searchPeople(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("query") query: String, // search text entered by user
        @Query("page") page: Int = 1, // page number for pagination
        @Query("language") language: String? = null // language code for localized results
    ): Response<PersonSearchResponse>


    // discover supports with_cast and with_crew for comprehensive search
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("sort_by") sortBy: String = "vote_average.desc", // sorting criteria
        @Query("vote_count.gte") minVotes: Int = 50, // minimum vote count filter
        @Query("with_cast") withCast: String? = null, // comma-separated cast member IDs
        @Query("with_crew") withCrew: String? = null, // comma-separated crew member IDs
        @Query("page") page: Int = 1, // page number for pagination
        @Query("with_genres") genres: String? = null, // comma-separated genre IDs
        @Query("primary_release_year") year: Int? = null, // release year filter
        @Query("vote_average.gte") minRating: Double? = null, // minimum rating filter
        @Query("vote_average.lte") maxRating: Double? = null, // maximum rating filter
        @Query("with_runtime.gte") minRuntime: Int? = null, // minimum runtime in minutes
        @Query("with_runtime.lte") maxRuntime: Int? = null, // maximum runtime in minutes
        @Query("language") language: String? = null // language code for localized results
    ): Response<MovieResponse>

    // Rate a movie (POST request)
    @POST("movie/{movie_id}/rating")
    suspend fun rateMovie(
        @Path("movie_id") movieId: Int, // unique movie identifier
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("guest_session_id") guestSessionId: String, // guest session ID for rating
        @Body rating: RatingRequest // rating data (value between 0.5 and 10.0)
    ): Response<RatingResponse>

    // Delete movie rating
    @DELETE("movie/{movie_id}/rating")
    suspend fun deleteMovieRating(
        @Path("movie_id") movieId: Int, // unique movie identifier
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("guest_session_id") guestSessionId: String // guest session ID for rating
    ): Response<RatingResponse>

    // Create guest session
    @GET("authentication/guest_session/new")
    suspend fun createGuestSession(
        @Query("api_key") apiKey: String // TMDB API key for authentication
    ): Response<GuestSessionResponse>

    // Get movie rating
    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieRating(
        @Path("movie_id") movieId: Int, // unique movie identifier
        @Query("api_key") apiKey: String, // TMDB API key for authentication
        @Query("guest_session_id") guestSessionId: String // guest session ID for rating
    ): Response<AccountStatesResponse>

}
