package com.example.popiflix.ui.home

import androidx.lifecycle.*
import com.example.popiflix.data.models.Movie
import com.example.popiflix.data.models.MovieDetails
import com.example.popiflix.data.repositories.MovieRepository
import com.example.popiflix.data.repository.FavoriteRepository
import com.example.popiflix.data.models.CreditsResponse
import com.example.popiflix.data.models.GuestSession
import com.example.popiflix.data.models.AccountStatesResponse
import android.widget.Toast
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import javax.inject.Inject

/**
 * ViewModel for home screen
 * Manages popular movies, new movies, upcoming movies, and movie details
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MovieRepository,
    private val favoritesRepo: FavoriteRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>(emptyList())
    val movies: LiveData<List<Movie>> = _movies
    
    
    private val _newMovies = MutableLiveData<List<Movie>>(emptyList())
    val newMovies: LiveData<List<Movie>> = _newMovies
    
    private val _upcomingMovies = MutableLiveData<List<Movie>>(emptyList())
    val upcomingMovies: LiveData<List<Movie>> = _upcomingMovies
    
    // Genre movies
    private val _actionAdventureMovies = MutableLiveData<List<Movie>>(emptyList())
    val actionAdventureMovies: LiveData<List<Movie>> = _actionAdventureMovies
    
    private val _comedyMovies = MutableLiveData<List<Movie>>(emptyList())
    val comedyMovies: LiveData<List<Movie>> = _comedyMovies
    
    private val _dramaMovies = MutableLiveData<List<Movie>>(emptyList())
    val dramaMovies: LiveData<List<Movie>> = _dramaMovies
    
    private val _romanceMovies = MutableLiveData<List<Movie>>(emptyList())
    val romanceMovies: LiveData<List<Movie>> = _romanceMovies
    
    private val _animationMovies = MutableLiveData<List<Movie>>(emptyList())
    val animationMovies: LiveData<List<Movie>> = _animationMovies
    
    private val _scienceFictionMovies = MutableLiveData<List<Movie>>(emptyList())
    val scienceFictionMovies: LiveData<List<Movie>> = _scienceFictionMovies
    
    private val _horrorMovies = MutableLiveData<List<Movie>>(emptyList())
    val horrorMovies: LiveData<List<Movie>> = _horrorMovies
    
    private val _thrillerMovies = MutableLiveData<List<Movie>>(emptyList())
    val thrillerMovies: LiveData<List<Movie>> = _thrillerMovies
    
    private val _documentaryMovies = MutableLiveData<List<Movie>>(emptyList())
    val documentaryMovies: LiveData<List<Movie>> = _documentaryMovies

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    

    private val _movieDetails = MutableLiveData<MovieDetails?>()
    val movieDetails: LiveData<MovieDetails?> = _movieDetails

    private val _movieCredits = MutableLiveData<CreditsResponse?>()
    val movieCredits: LiveData<CreditsResponse?> = _movieCredits
    
    
    // Guest session for rating
    private var guestSession: GuestSession? = null
    
    init {
        // Load saved guest session
        loadGuestSession()
    }
    
    // Rating status
    private val _ratingStatus = MutableLiveData<String?>()
    val ratingStatus: LiveData<String?> = _ratingStatus
    
    // Current movie rating
    private val _currentRating = MutableLiveData<Double?>()
    val currentRating: LiveData<Double?> = _currentRating
    

    // pagination
    private var currentPage = 1
    private var totalPages = 1
    private var isLoadingMore = false
    
    // Track current language to detect changes
    private var currentLanguage: String? = null

    init {
        // Load data automatically
        _loading.value = true
        loadPopularMovies(1)
        loadNewMovies(1)
        loadUpcomingMovies(1)
        loadAllGenreMovies()
    }

    /**
     * Load popular movies from API
     * Supports pagination and error handling
     */
    fun loadPopularMovies(page: Int = 1) {
        if (isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            if (page == 1) {
                _loading.value = true
                _error.value = null  // Clear any previous errors
            }
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getPopularMovies(apiKey, page)
                val currentList = _movies.value?.toMutableList() ?: mutableListOf()
                currentList.addAll(response.results)
                _movies.value = currentList
                currentPage = page
                totalPages = response.totalPages
                _error.value = null
                
                // Update current language after successful load
                updateCurrentLanguage()
            } catch (e: Exception) {
                _error.value = e.message ?: "שגיאה בטעינה"
            } finally {
                _loading.value = false
                isLoadingMore = false
            }
        }
    }
    
    
    /**
     * Load new movies (now playing in cinemas)
     */
    fun loadNewMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getNowPlayingMovies(apiKey, page)
                _newMovies.value = response.results
            } catch (e: Exception) {
                // Don't show error for new movies, just log it
                println("Failed to load new movies: ${e.message}")
            }
        }
    }
    
    /**
     * Load upcoming movies
     */
    fun loadUpcomingMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getUpcomingMovies(apiKey, page)
                _upcomingMovies.value = response.results
            } catch (e: Exception) {
                // Don't show error for upcoming movies, just log it
                println("Failed to load upcoming movies: ${e.message}")
            }
        }
    }
    
    /**
     * Check if language has changed and reload data if needed
     */
    fun checkLanguageChange() {
        val newLanguage = getCurrentLanguage()
        if (currentLanguage != null && currentLanguage != newLanguage) {
            // Language changed, reload data
            clearMovies()
            loadPopularMovies(1)
            loadNewMovies(1)
            loadUpcomingMovies(1)
        }
        currentLanguage = newLanguage
    }
    
    /**
     * Update current language tracking
     */
    private fun updateCurrentLanguage() {
        currentLanguage = getCurrentLanguage()
    }
    
    /**
     * Get current app language
     */
    private fun getCurrentLanguage(): String {
        return androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: java.util.Locale.getDefault().language
    }

    
    /**
     * Clear current movies list and reset pagination
     * Used when language changes to reload data with new language
     */
    fun clearMovies() {
        _movies.value = emptyList()
        currentPage = 1
        totalPages = 1
        isLoadingMore = false
    }
    
    /**
     * Load all genre movies
     */
    private fun loadAllGenreMovies() {
        viewModelScope.launch {
            try {
                // Load genre movies in parallel for better performance
                val genreJobs = listOf(
                    async { loadActionAdventureMovies(1) },
                    async { loadComedyMovies(1) },
                    async { loadDramaMovies(1) },
                    async { loadRomanceMovies(1) },
                    async { loadAnimationMovies(1) },
                    async { loadScienceFictionMovies(1) },
                    async { loadHorrorMovies(1) },
                    async { loadThrillerMovies(1) },
                    async { loadDocumentaryMovies(1) }
                )
                
                // Wait for all genre movies to load
                genreJobs.awaitAll()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    /**
     * Reload all movie data
     * Called when background update completes
     */
    fun reloadAllMovies() {
        loadPopularMovies(1)
        loadNewMovies(1)
        loadUpcomingMovies(1)
        loadAllGenreMovies()
    }
    

    /**
     * Load action and adventure movies from API
     * Used for genre-specific carousel
     */
    fun loadActionAdventureMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getActionAdventureMovies(apiKey, page)
                _actionAdventureMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load action adventure movies: ${e.message}")
            }
        }
    }

    /**
     * Load comedy movies from API
     * Used for genre-specific carousel
     */
    fun loadComedyMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getComedyMovies(apiKey, page)
                _comedyMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load comedy movies: ${e.message}")
            }
        }
    }

    /**
     * Load drama movies from API
     * Used for genre-specific carousel
     */
    fun loadDramaMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getDramaMovies(apiKey, page)
                _dramaMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load drama movies: ${e.message}")
            }
        }
    }

    /**
     * Load romance movies from API
     * Used for genre-specific carousel
     */
    fun loadRomanceMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getRomanceMovies(apiKey, page)
                _romanceMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load romance movies: ${e.message}")
            }
        }
    }

    /**
     * Load animation movies from API
     * Used for genre-specific carousel
     */
    fun loadAnimationMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getAnimationMovies(apiKey, page)
                _animationMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load animation movies: ${e.message}")
            }
        }
    }

    /**
     * Load science fiction movies from API
     * Used for genre-specific carousel
     */
    fun loadScienceFictionMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getScienceFictionMovies(apiKey, page)
                _scienceFictionMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load science fiction movies: ${e.message}")
            }
        }
    }

    /**
     * Load horror movies from API
     * Used for genre-specific carousel
     */
    fun loadHorrorMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getHorrorMovies(apiKey, page)
                _horrorMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load horror movies: ${e.message}")
            }
        }
    }

    /**
     * Load thriller movies from API
     * Used for genre-specific carousel
     */
    fun loadThrillerMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getThrillerMovies(apiKey, page)
                _thrillerMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load thriller movies: ${e.message}")
            }
        }
    }

    /**
     * Load documentary movies from API
     * Used for genre-specific carousel
     */
    fun loadDocumentaryMovies(page: Int = 1) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val response = repo.getDocumentaryMovies(apiKey, page)
                _documentaryMovies.value = response.results
            } catch (e: Exception) {
                println("Failed to load documentary movies: ${e.message}")
            }
        }
    }

    /**
     * Add movie to favorites list
     * Saves movie to local database
     */
    fun addToFavorites(movie: Movie) {
        viewModelScope.launch {
            favoritesRepo.addToFavorites(movie)
        }
    }

    /**
     * Get detailed information about a specific movie by ID
     * Used by MovieDetailFragment to display comprehensive movie information
     * 
     * @param movieId The TMDB movie ID to fetch details for
     */
    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val details = repo.getMovieDetails(movieId, apiKey)
                _movieDetails.value = details
                
                // Also fetch movie credits for cast and crew information
                getMovieCredits(movieId)
            } catch (e: Exception) {
                _error.value = e.message ?: "שגיאה בטעינת פרטי הסרט"
            }
        }
    }

    /**
     * Get movie credits (cast and crew) for a specific movie
     * 
     * @param movieId The TMDB movie ID to fetch credits for
     */
    fun getMovieCredits(movieId: Int) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val credits = repo.getMovieCredits(movieId, apiKey)
                _movieCredits.value = credits
            } catch (e: Exception) {
                // Don't show error for credits, just log it
                println("Failed to load movie credits: ${e.message}")
            }
        }
    }


    /**
     * Clear the current movie details from memory
     * Called when leaving MovieDetailFragment to free up resources
     */
    fun clearMovieDetails() {
        _movieDetails.value = null
    }

    /**
     * Rate a movie (POST request)
     * @param movieId The TMDB movie ID
     * @param rating The rating value (0.5 to 10.0)
     */
    fun rateMovie(movieId: Int, rating: Double) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                
                // Get or create guest session
                if (guestSession == null) {
                    guestSession = repo.createGuestSession(apiKey)
                }
                
                val sessionId = guestSession?.guestSessionId
                if (sessionId != null) {
                    val response = repo.rateMovie(movieId, rating, apiKey, sessionId)
                    if (response.success) {
                        // Show success message or update UI
                        println("Movie rated successfully: $rating stars (Guest Session: $sessionId)")
                        _ratingStatus.value = "success:$rating"
                        _currentRating.value = rating
                    } else {
                        println("Failed to rate movie: ${response.statusMessage}")
                        _ratingStatus.value = "error:${response.statusMessage}"
                    }
                } else {
                    println("Error: No guest session available")
                    _ratingStatus.value = "error:No guest session available"
                }
            } catch (e: Exception) {
                println("Error rating movie: ${e.message}")
                _ratingStatus.value = "error:${e.message}"
            }
        }
    }

    /**
     * Delete movie rating (DELETE request)
     * @param movieId The TMDB movie ID
         */
    fun deleteMovieRating(movieId: Int) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                
                // Get or create guest session
                if (guestSession == null) {
                    guestSession = repo.createGuestSession(apiKey)
                }
                
                val sessionId = guestSession?.guestSessionId
                if (sessionId != null) {
                    val response = repo.deleteMovieRating(movieId, apiKey, sessionId)
                    if (response.success) {
                        println("Movie rating deleted successfully (Guest Session: $sessionId)")
                        _currentRating.value = null
                    } else {
                        println("Failed to delete movie rating: ${response.statusMessage}")
                    }
                } else {
                    println("Error: No guest session available")
                }
            } catch (e: Exception) {
                println("Error deleting movie rating: ${e.message}")
            }
        }
    }

    /**
     * Get current movie rating
     */
    fun getCurrentMovieRating(movieId: Int) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                
                // Get or create guest session
                if (guestSession == null) {
                    guestSession = repo.createGuestSession(apiKey)
                }
                
                val sessionId = guestSession?.guestSessionId
                if (sessionId != null) {
                    val response = repo.getMovieRating(movieId, apiKey, sessionId)
                    _currentRating.value = response.rating
                    println("Current movie rating: ${response.rating}")
                } else {
                    println("Error: No guest session available")
                }
            } catch (e: Exception) {
                println("Error getting movie rating: ${e.message}")
                _currentRating.value = null
            }
        }
    }
    
    private fun loadGuestSession() {
        val sessionId = sharedPreferences.getString("guest_session_id", null)
        val expiresAt = sharedPreferences.getString("guest_session_expires_at", null)
        if (sessionId != null && expiresAt != null) {
            guestSession = GuestSession(
                success = true,
                guestSessionId = sessionId,
                expiresAt = expiresAt
            )
            println("Loaded saved guest session: $sessionId")
        }
    }
    
    private fun saveGuestSession(session: GuestSession) {
        sharedPreferences.edit()
            .putString("guest_session_id", session.guestSessionId)
            .putString("guest_session_expires_at", session.expiresAt)
            .apply()
        println("Saved guest session: ${session.guestSessionId}")
    }

}
