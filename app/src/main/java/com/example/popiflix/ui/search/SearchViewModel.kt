package com.example.popiflix.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popiflix.data.models.Movie
import com.example.popiflix.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for search screen
 * Manages movie search functionality with debounce and pagination
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>(emptyList())
    val movies: LiveData<List<Movie>> = _movies

    // Pagination state
    private var currentSearchPage = 1
    private var totalSearchPages = 1
    private var isPaginationLoading = false
    private var currentSearchQuery = ""

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching

    // Job for debounce (delay between keystrokes)
    private var searchJob: Job? = null

    /**
     * Live search: each time user types, waits 300ms before firing API call.
     * If user types again within 300ms, previous job is cancelled.
     */
    fun searchMovies(query: String) {
        currentSearchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce 300ms
            if (query.isBlank()) {
                clearSearch()
                return@launch
            }

            try {
                _loading.value = true
                _error.value = null
                _isSearching.value = true
                currentSearchPage = 1

                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val (combinedResults, totalPages) = repo.searchCombined(query, apiKey, page = 1)

                if (combinedResults.isEmpty()) {
                    _movies.value = emptyList()
                    _error.value = "No results found"
                    totalSearchPages = 1
                } else {
                    // Results are already sorted by Repository with smart prioritization
                    _movies.value = combinedResults
                    totalSearchPages = totalPages
                    _error.value = null
                }
            } catch (e: Exception) {
                _movies.value = emptyList()
                _error.value = e.message ?: "Search error"
                _isSearching.value = false
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Clears current search state and results.
     */
    fun clearSearch() {
        _isSearching.value = false
        _movies.value = emptyList()
        currentSearchPage = 1
        totalSearchPages = 1
        currentSearchQuery = ""
        _error.value = null
    }

    /**
     * Loads the next page using the same combined search strategy.
     * Keeps the global sort by TMDB voteAverage (descending) and avoids duplicates.
     */
    fun loadNextSearchPage() {
        if (isPaginationLoading || currentSearchPage >= totalSearchPages || currentSearchQuery.isBlank()) {
            return
        }

        _isLoadingMore.value = true
        isPaginationLoading = true

        viewModelScope.launch {
            try {
                val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                val nextPage = currentSearchPage + 1
                val (combinedResults, _) = repo.searchCombined(currentSearchQuery, apiKey, page = nextPage)

                val currentList = _movies.value?.toMutableList() ?: mutableListOf()
                // Append only items that are not already present
                val newOnes = combinedResults.filter { newM -> currentList.none { it.id == newM.id } }
                currentList.addAll(newOnes)
                // Results are already sorted by Repository with smart prioritization
                _movies.value = currentList

                currentSearchPage = nextPage
            } catch (_: Exception) {
                // Ignore pagination errors to avoid breaking the UI
            } finally {
                _isLoadingMore.value = false
                isPaginationLoading = false
            }
        }
    }
}
