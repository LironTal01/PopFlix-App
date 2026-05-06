package com.example.popiflix.ui.search

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popiflix.R
import com.example.popiflix.ui.home.MoviesAdapter
import com.example.popiflix.ui.search.PopularSearchesAdapter
import com.example.popiflix.data.models.PopularSearchesData
import com.example.popiflix.data.repositories.SearchHistoryRepository
import dagger.hilt.android.AndroidEntryPoint
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

/**
 * Search screen fragment
 * Handles movie search functionality with debounce and pagination
 */
@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    // ViewModel for handling search logic and data
    private val viewModel: SearchViewModel by viewModels()
    
    // Repository for managing search history
    @Inject lateinit var searchHistoryRepository: SearchHistoryRepository
    
    // Adapters for different RecyclerViews
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var popularSearchesAdapter: PopularSearchesAdapter
    private lateinit var recentSearchesAdapter: PopularSearchesAdapter
    
    // UI components
    private lateinit var searchInput: TextInputEditText
    private lateinit var progress: ProgressBar
    private lateinit var errorText: TextView

    /**
     * Called when the fragment's view is created.
     * Sets up all UI components, adapters, and observers.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("SearchFragment: onViewCreated called")

        // Initialize all UI views
        val popularMoviesRecyclerView = view.findViewById<RecyclerView>(R.id.popularMoviesRecyclerView)
        val popularSearchesRecyclerView = view.findViewById<RecyclerView>(R.id.popularSearchesRecyclerView)
        val recentSearchesRecyclerView = view.findViewById<RecyclerView>(R.id.recentSearchesRecyclerView)
        searchInput = view.findViewById(R.id.searchInput)
        progress = view.findViewById(R.id.progress)
        val loadingMoreProgress = view.findViewById<ProgressBar>(R.id.loadingMoreProgress)
        errorText = view.findViewById(R.id.errorText)

        // Set up adapter for displaying search results (movies)
        popularMoviesAdapter = MoviesAdapter(
            emptyList(),
            onClick = { movie ->
                val action = SearchFragmentDirections.actionSearchToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )

        // Set up adapter for popular searches (trending searches)
        popularSearchesAdapter = PopularSearchesAdapter(
            onSearchClick = { query ->
                // When user clicks on popular search, perform the search
                searchInput.setText(query)
                viewModel.searchMovies(query)
                searchHistoryRepository.addSearchToHistory(query)
            }
        )

        // Set up adapter for recent searches with clear functionality
        recentSearchesAdapter = PopularSearchesAdapter(
            onSearchClick = { query ->
                // When user clicks on recent search, perform the search
                searchInput.setText(query)
                viewModel.searchMovies(query)
                searchHistoryRepository.addSearchToHistory(query)
            },
            onClearClick = { query ->
                // When user clicks clear button, remove from history
                searchHistoryRepository.removeSearchFromHistory(query)
                loadRecentSearches() // Refresh the list
            }
        )

        // Set up GridLayoutManager for movies - adaptive columns based on orientation
        val orientation = resources.configuration.orientation
        val columns = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        val gridLayoutManager = GridLayoutManager(requireContext(), columns)
        popularMoviesRecyclerView.layoutManager = gridLayoutManager
        popularMoviesRecyclerView.adapter = popularMoviesAdapter

        // Set up LinearLayoutManager for popular searches (horizontal scrolling)
        val popularLinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularSearchesRecyclerView.layoutManager = popularLinearLayoutManager
        popularSearchesRecyclerView.adapter = popularSearchesAdapter

        // Set up LinearLayoutManager for recent searches - horizontal in landscape, vertical in portrait
        val recentOrientation = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 
            RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        val recentLinearLayoutManager = LinearLayoutManager(requireContext(), recentOrientation, false)
        recentSearchesRecyclerView.layoutManager = recentLinearLayoutManager
        recentSearchesRecyclerView.adapter = recentSearchesAdapter

        // Pagination scroll listener
        popularMoviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = gridLayoutManager.childCount
                val totalItemCount = gridLayoutManager.itemCount
                val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 3) &&
                    firstVisibleItemPosition >= 0 && totalItemCount > 0
                ) {
                    viewModel.loadNextSearchPage()
                }
            }
        })

        // Observe movies LiveData
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            popularMoviesAdapter.submitList(movies)
        }

        // Observe loading
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) errorText.visibility = View.GONE
        }

        // Observe loading more
        viewModel.isLoadingMore.observe(viewLifecycleOwner) { isLoadingMore ->
            loadingMoreProgress.visibility = if (isLoadingMore) View.VISIBLE else View.GONE
        }

        // Observe search state to update visibility
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            val popularSearchesSection = view.findViewById<LinearLayout>(R.id.popularSearchesSection)
            val recentSearchesSection = view.findViewById<LinearLayout>(R.id.recentSearchesSection)
            
            // Show/hide search sections based on search state
            popularSearchesSection?.visibility = if (isSearching) View.GONE else View.VISIBLE
            recentSearchesSection?.visibility = if (isSearching) View.GONE else View.VISIBLE
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            errorText.visibility = if (error != null) View.VISIBLE else View.GONE
            errorText.text = error ?: ""
        }

        // Load popular searches
        popularSearchesAdapter.submitList(PopularSearchesData.popularSearches)
        
        // Load recent searches
        loadRecentSearches()

        // Set up search input
        setupSearch()
        
        // Set up clear button functionality
        setupClearButton()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Update grid layout when orientation changes
        updateGridLayout()
    }

    private fun setupSearch() {
        // Action Search in keyboard
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        // Live search for every character typed
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                if (query.isNotEmpty()) {
                    viewModel.searchMovies(query)
                } else {
                    viewModel.clearSearch()
                }
            }
        })
    }

    private fun performSearch() {
        val query = searchInput.text.toString().trim()
        if (query.isNotEmpty()) {
            errorText.visibility = View.GONE
            viewModel.searchMovies(query)
            searchHistoryRepository.addSearchToHistory(query)
        }
    }

    private fun loadRecentSearches() {
        val recentSearches = searchHistoryRepository.getRecentSearches()
        recentSearchesAdapter.submitList(recentSearches)
        
        // Show/hide recent searches section based on whether there are recent searches
        val recentSearchesSection = view?.findViewById<LinearLayout>(R.id.recentSearchesSection)
        recentSearchesSection?.visibility = if (recentSearches.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupClearButton() {
        val searchInputLayout = view?.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.searchInputLayout)
        searchInputLayout?.setEndIconOnClickListener {
            // Clear search input
            searchInput.setText("")
            // Clear search results and return to main search page
            viewModel.clearSearch()
            // Reload recent searches to show updated list
            loadRecentSearches()
        }
    }

    /**
     * Update grid layout when orientation changes
     */
    private fun updateGridLayout() {
        val orientation = resources.configuration.orientation
        val columns = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        val gridLayoutManager = GridLayoutManager(requireContext(), columns)
        val popularMoviesRecyclerView = view?.findViewById<RecyclerView>(R.id.popularMoviesRecyclerView)
        popularMoviesRecyclerView?.layoutManager = gridLayoutManager
        
        // Update recent searches orientation too
        val recentOrientation = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 
            RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        val recentLinearLayoutManager = LinearLayoutManager(requireContext(), recentOrientation, false)
        val recentSearchesRecyclerView = view?.findViewById<RecyclerView>(R.id.recentSearchesRecyclerView)
        recentSearchesRecyclerView?.layoutManager = recentLinearLayoutManager
    }
}
