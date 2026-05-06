package com.example.popiflix.ui.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.content.SharedPreferences
import com.example.popiflix.data.database.WatchlistMovie
import com.example.popiflix.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * ViewModel for watchlist screen
 * Manages watchlist movies list and operations
 */
@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repo: WatchlistRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _watchlist = MutableLiveData<List<WatchlistMovie>>()
    val watchlist: LiveData<List<WatchlistMovie>> = _watchlist
    
    private var allMovies: List<WatchlistMovie> = emptyList()
    
    // Track current language to detect changes
    private var currentLanguage: String? = null
    
    private val prefs: SharedPreferences = context.getSharedPreferences("watchlist_prefs", Context.MODE_PRIVATE)
    private val SORT_TYPE_KEY = "sort_type"
    private val DEFAULT_SORT_TYPE = "date_added"

    init {
        loadAllMovies()
        applySavedSort()
        // Update watchlist for current language
        viewModelScope.launch {
            repo.updateWatchlistForCurrentLanguage()
        }
    }
    
    /**
     * Check if language has changed and update saved items if needed
     */
    fun checkLanguageChange() {
        val newLanguage = getCurrentLanguage()
        if (currentLanguage != null && currentLanguage != newLanguage) {
            // Language changed, update saved items
            viewModelScope.launch {
                repo.updateWatchlistForCurrentLanguage()
            }
        }
        currentLanguage = newLanguage
    }
    
    /**
     * Get current app language
     */
    private fun getCurrentLanguage(): String {
        return androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: java.util.Locale.getDefault().language
    }
    
    private fun loadAllMovies() {
        viewModelScope.launch {
            repo.getAllWatchlistMovies().observeForever { movies ->
                allMovies = movies ?: emptyList()
                applySavedSort()
            }
        }
    }
    
    private fun applySavedSort() {
        val sortType = prefs.getString(SORT_TYPE_KEY, DEFAULT_SORT_TYPE)
        when (sortType) {
            "date_added" -> sortByDateAdded()
            "alphabetical" -> sortByAlphabetical()
            "release_date" -> sortByReleaseDate()
            "rating" -> sortByRating()
            else -> sortByDateAdded()
        }
    }
    
    fun searchByNotes(query: String) {
        if (query.isBlank()) {
            _watchlist.value = allMovies
        } else {
            val filteredMovies = allMovies.filter { movie ->
                movie.userNotes?.contains(query, ignoreCase = true) == true
            }
            _watchlist.value = filteredMovies
        }
    }
    
    fun sortByDateAdded() {
        val sortedMovies = allMovies.sortedByDescending { it.dateAdded }
        _watchlist.value = sortedMovies
        prefs.edit().putString(SORT_TYPE_KEY, "date_added").apply()
    }
    
    fun sortByAlphabetical() {
        val sortedMovies = allMovies.sortedBy { it.title }
        _watchlist.value = sortedMovies
        prefs.edit().putString(SORT_TYPE_KEY, "alphabetical").apply()
    }
    
    fun sortByReleaseDate() {
        val sortedMovies = allMovies.sortedByDescending { it.releaseDate }
        _watchlist.value = sortedMovies
        prefs.edit().putString(SORT_TYPE_KEY, "release_date").apply()
    }
    
    fun sortByRating() {
        val sortedMovies = allMovies.sortedByDescending { it.voteAverage }
        _watchlist.value = sortedMovies
        prefs.edit().putString(SORT_TYPE_KEY, "rating").apply()
    }

    fun remove(movie: WatchlistMovie) = viewModelScope.launch {
        repo.removeFromWatchlist(movie)
    }
    
    fun addToWatchlist(movie: WatchlistMovie) = viewModelScope.launch {
        // Simply update the watchlist movie to restore it
        repo.updateWatchlistMovie(movie)
    }
    
    fun getCurrentSortType(): String {
        return prefs.getString(SORT_TYPE_KEY, DEFAULT_SORT_TYPE) ?: DEFAULT_SORT_TYPE
    }
    
    fun updateMovieNotes(movieId: Int, notes: String?) = viewModelScope.launch {
        repo.updateNotes(movieId, notes)
    }
}
