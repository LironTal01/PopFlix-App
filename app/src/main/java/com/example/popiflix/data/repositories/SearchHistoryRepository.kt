package com.example.popiflix.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.popiflix.data.models.PopularSearch
import com.example.popiflix.data.models.SearchType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for search history operations
 * Handles database operations for search history
 */
@Singleton
class SearchHistoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val maxHistorySize = 5
    
    /**
     * Add a search query to history
     */
    fun addSearchToHistory(query: String) {
        if (query.isBlank()) return // don't add empty queries
        
        val currentHistory = getRecentSearches() // get existing history
        val newHistory = (listOf(query) + currentHistory.map { it.query }.filter { it != query }).take(maxHistorySize) // add new query and remove duplicates
        
        val historyString = newHistory.joinToString("|") // join with pipe separator
        prefs.edit().putString("recent_searches", historyString).apply() // save to SharedPreferences
    }
    
    /**
     * Get recent searches as PopularSearch objects
     */
    fun getRecentSearches(): List<PopularSearch> {
        val historyString = prefs.getString("recent_searches", "") ?: "" // get history from SharedPreferences
        if (historyString.isEmpty()) return emptyList() // return empty list if no history
        
        return historyString.split("|").map { query -> // split by pipe separator
            PopularSearch(
                query = query, // search query
                type = SearchType.MOVIE, // Default type for recent searches
                emoji = "🕒" // Clock emoji for recent searches
            )
        }
    }
    
    /**
     * Remove a specific search from history
     */
    fun removeSearchFromHistory(query: String) {
        val currentHistory = getRecentSearches() // get current history
        val filteredHistory = currentHistory.filter { it.query != query } // remove the specified query
        
        val historyString = filteredHistory.map { it.query }.joinToString("|") // join remaining queries
        prefs.edit().putString("recent_searches", historyString).apply() // save updated history
    }

    /**
     * Clear all search history
     */
    fun clearSearchHistory() {
        prefs.edit().remove("recent_searches").apply() // remove all search history from SharedPreferences
    }
    
    /**
     * Check if there are any recent searches
     */
    fun hasRecentSearches(): Boolean {
        return getRecentSearches().isNotEmpty() // check if there are any recent searches
    }
}
