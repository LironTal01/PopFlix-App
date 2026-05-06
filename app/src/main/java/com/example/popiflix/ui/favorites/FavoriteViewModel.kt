package com.example.popiflix.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.popiflix.data.database.FavoriteMovie
import com.example.popiflix.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * ViewModel for favorites screen
 * Manages favorite movies list and operations
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repo: FavoriteRepository
) : ViewModel() {

    // LiveData list of favorite movies (auto-updated from Room database)
    val favorites: LiveData<List<FavoriteMovie>> = repo.getAllFavorites()
    
    // Track current language to detect changes
    private var currentLanguage: String? = null

    init {
        // Update existing movies titles if needed
        viewModelScope.launch {
            repo.updateFavoritesForCurrentLanguage()
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
                repo.updateFavoritesForCurrentLanguage()
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

    // Remove a movie from favorites
    fun remove(movie: FavoriteMovie) = viewModelScope.launch {
        repo.removeFromFavorites(movie)
    }
    
    // Add a movie back to favorites (for undo functionality)
    fun addToFavorites(movie: FavoriteMovie) = viewModelScope.launch {
        repo.addToFavorites(movie)
    }
}
