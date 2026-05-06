package com.example.popiflix.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.PagerSnapHelper
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popiflix.R
import com.example.popiflix.ui.home.MoviesAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * Home screen fragment
 * Displays popular movies in a RecyclerView with search functionality
 */
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var heroAdapter: HeroAdapter
    private lateinit var newMoviesAdapter: HorizontalMoviesAdapter
    private lateinit var popularMoviesAdapter: HorizontalMoviesAdapter
    private lateinit var upcomingMoviesAdapter: HorizontalMoviesAdapter
    
    // Genre adapters
    private lateinit var actionAdventureAdapter: HorizontalMoviesAdapter
    private lateinit var comedyAdapter: HorizontalMoviesAdapter
    private lateinit var dramaAdapter: HorizontalMoviesAdapter
    private lateinit var romanceAdapter: HorizontalMoviesAdapter
    private lateinit var animationAdapter: HorizontalMoviesAdapter
    private lateinit var scienceFictionAdapter: HorizontalMoviesAdapter
    private lateinit var horrorAdapter: HorizontalMoviesAdapter
    private lateinit var thrillerAdapter: HorizontalMoviesAdapter
    private lateinit var documentaryAdapter: HorizontalMoviesAdapter
    private lateinit var heroRecycler: RecyclerView
    private lateinit var pageIndicators: LinearLayout
    private var currentPosition = 0
    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    private var realMovieCount = 0

    /**
     * Initialize UI components and setup adapters
     * Called when view is created
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        heroRecycler = view.findViewById<RecyclerView>(R.id.heroRecycler)
        val newMoviesRecycler = view.findViewById<RecyclerView>(R.id.newMoviesRecycler)
        val popularMoviesRecycler = view.findViewById<RecyclerView>(R.id.popularMoviesRecycler)
        val upcomingMoviesRecycler = view.findViewById<RecyclerView>(R.id.upcomingMoviesRecycler)
        
        // Genre recyclers
        val actionAdventureRecycler = view.findViewById<RecyclerView>(R.id.actionAdventureRecycler)
        val comedyRecycler = view.findViewById<RecyclerView>(R.id.comedyRecycler)
        val dramaRecycler = view.findViewById<RecyclerView>(R.id.dramaRecycler)
        val romanceRecycler = view.findViewById<RecyclerView>(R.id.romanceRecycler)
        val animationRecycler = view.findViewById<RecyclerView>(R.id.animationRecycler)
        val scienceFictionRecycler = view.findViewById<RecyclerView>(R.id.scienceFictionRecycler)
        val horrorRecycler = view.findViewById<RecyclerView>(R.id.horrorRecycler)
        val thrillerRecycler = view.findViewById<RecyclerView>(R.id.thrillerRecycler)
        val documentaryRecycler = view.findViewById<RecyclerView>(R.id.documentaryRecycler)
        pageIndicators = view.findViewById<LinearLayout>(R.id.pageIndicators)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val errorText = view.findViewById<TextView>(R.id.errorText)

        // Show progress bar initially
        progress.visibility = View.VISIBLE

        // Setup hero carousel
        setupHeroCarousel()


        // Setup new movies adapter
        newMoviesAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        newMoviesRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        newMoviesRecycler.adapter = newMoviesAdapter

        // Setup popular movies adapter
        popularMoviesAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        popularMoviesRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularMoviesRecycler.adapter = popularMoviesAdapter

        // Setup upcoming movies adapter
        upcomingMoviesAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        upcomingMoviesRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        upcomingMoviesRecycler.adapter = upcomingMoviesAdapter

        // Initialize genre adapters
        actionAdventureAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        comedyAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        dramaAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        romanceAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        animationAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        scienceFictionAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        horrorAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        thrillerAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        documentaryAdapter = HorizontalMoviesAdapter(
            emptyList(),
            onMovieClick = { movie ->
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )

        // Setup genre adapters
        setupGenreAdapter(actionAdventureAdapter, actionAdventureRecycler)
        setupGenreAdapter(comedyAdapter, comedyRecycler)
        setupGenreAdapter(dramaAdapter, dramaRecycler)
        setupGenreAdapter(romanceAdapter, romanceRecycler)
        setupGenreAdapter(animationAdapter, animationRecycler)
        setupGenreAdapter(scienceFictionAdapter, scienceFictionRecycler)
        setupGenreAdapter(horrorAdapter, horrorRecycler)
        setupGenreAdapter(thrillerAdapter, thrillerRecycler)
        setupGenreAdapter(documentaryAdapter, documentaryRecycler)

        // Observe ViewModel data
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe popular movies (for hero and popular worldwide)
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            // Show first 5 movies in hero section
            val heroMovies = movies.take(5)
            heroAdapter.updateMovies(heroMovies)
            realMovieCount = heroMovies.size
            setupPageIndicators(realMovieCount)
            
            // Start auto scroll after data is loaded
            if (realMovieCount > 1) {
                startAutoScroll()
            }

            // Show first 10 popular movies in popular movies section
            val popularMovies = movies.take(10)
            popularMoviesAdapter.updateMovies(popularMovies)
            
            // Add fade in animation for hero section
            heroRecycler.startAnimation(android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
        }
        
        
        // Observe new movies (for new in cinemas)
        viewModel.newMovies.observe(viewLifecycleOwner) { newMovies ->
            // Show first 10 new movies in new movies section
            val newMoviesList = newMovies.take(10)
            newMoviesAdapter.updateMovies(newMoviesList)
        }
        
        // Observe upcoming movies
        viewModel.upcomingMovies.observe(viewLifecycleOwner) { upcomingMovies ->
            // Show first 10 upcoming movies in upcoming movies section
            val upcomingMoviesList = upcomingMovies.take(10)
            upcomingMoviesAdapter.updateMovies(upcomingMoviesList)
        }
        
        // Observe genre movies
        viewModel.actionAdventureMovies.observe(viewLifecycleOwner) { movies ->
            actionAdventureAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.comedyMovies.observe(viewLifecycleOwner) { movies ->
            comedyAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.dramaMovies.observe(viewLifecycleOwner) { movies ->
            dramaAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.romanceMovies.observe(viewLifecycleOwner) { movies ->
            romanceAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.animationMovies.observe(viewLifecycleOwner) { movies ->
            animationAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.scienceFictionMovies.observe(viewLifecycleOwner) { movies ->
            scienceFictionAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.horrorMovies.observe(viewLifecycleOwner) { movies ->
            horrorAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.thrillerMovies.observe(viewLifecycleOwner) { movies ->
            thrillerAdapter.updateMovies(movies.take(10))
        }
        
        viewModel.documentaryMovies.observe(viewLifecycleOwner) { movies ->
            documentaryAdapter.updateMovies(movies.take(10))
        }

        viewModel.error.observe(viewLifecycleOwner) { err ->
            errorText.visibility = if (err != null) View.VISIBLE else View.GONE
            errorText.text = err ?: ""
        }
    }
    
    /**
     * Setup genre adapter with RecyclerView
     */
    private fun setupGenreAdapter(adapter: HorizontalMoviesAdapter, recyclerView: RecyclerView) {
        // Set horizontal layout manager for horizontal scrolling
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        // Set adapter to display movies
        recyclerView.adapter = adapter
    }

    
    /**
     * Handle configuration changes (language, orientation)
     * Reloads data when language changes
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload data when configuration changes (e.g., language change)
        reloadData()
    }
    
    /**
     * Reload data when language changes
     */
    private fun reloadData() {
        // Clear current movies and reload with new language
        viewModel.clearMovies() // Reset pagination and clear list
        viewModel.loadPopularMovies(1) // Load first page with new language
    }

    /**
     * Setup hero carousel with all its functionality
     */
    private fun setupHeroCarousel() {
        // Setup hero adapter with click navigation
        heroAdapter = HeroAdapter(
            emptyList(), // Start with empty list, will be populated later
            onMovieClick = { movie ->
                // Navigate to movie detail screen when clicked
                val action = HomeFragmentDirections.actionHomeToMovieDetail(movie.id)
                findNavController().navigate(action)
            }
        )
        // Set horizontal layout manager for horizontal scrolling
        heroRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        heroRecycler.adapter = heroAdapter
        
        // Add PagerSnapHelper for smooth scrolling (snaps to items)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(heroRecycler)
        
        // Add scroll listener to handle manual scrolling
        heroRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // User is manually scrolling, stop auto scroll temporarily
                    stopAutoScroll()
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Update current position based on current view
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    layoutManager?.let {
                        // Get position of first visible item
                        currentPosition = it.findFirstVisibleItemPosition()
                        // Handle invalid position
                        if (currentPosition == RecyclerView.NO_POSITION) {
                            currentPosition = 0
                        }
                        // Update page indicators to show current position
                        updatePageIndicators(currentPosition)
                    }
                    
                    // Scrolling stopped, restart auto scroll after delay
                    handler.postDelayed({
                        // Only restart if we have movies and auto scroll is not running
                        if (realMovieCount > 1 && autoScrollRunnable == null) {
                            startAutoScroll()
                        }
                    }, 1000) // 1 second delay before restarting auto scroll after user stops manual scrolling
                }
            }
        })
    }

    /**
     * Setup page indicators for hero carousel
     */
    private fun setupPageIndicators(count: Int) {
        // Remove all existing indicators
        pageIndicators.removeAllViews()
        // Create indicators for each movie
        for (i in 0 until count) {
            val indicator = View(requireContext())
            // Get size and margin from resources
            val size = resources.getDimensionPixelSize(R.dimen.page_indicator_size)
            val margin = resources.getDimensionPixelSize(R.dimen.page_indicator_margin)
            
            // Set layout parameters
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(margin, 0, margin, 0) // Left and right margins
            indicator.layoutParams = params
            // Set unselected background
            indicator.setBackgroundResource(R.drawable.page_indicator_unselected)
            // Add to container
            pageIndicators.addView(indicator)
        }
        // Set first indicator as selected
        updatePageIndicators(0)
    }

    /**
     * Update page indicators to show current position
     */
    private fun updatePageIndicators(position: Int) {
        // Update all indicators based on current position
        for (i in 0 until pageIndicators.childCount) {
            val indicator = pageIndicators.getChildAt(i)
            // Set selected background for current position, unselected for others
            indicator.setBackgroundResource(
                if (i == position) R.drawable.page_indicator_selected 
                else R.drawable.page_indicator_unselected
            )
        }
    }

    /**
     * Stop auto scroll
     */
    private fun stopAutoScroll() {
        // Remove any pending auto scroll callbacks
        autoScrollRunnable?.let { handler.removeCallbacks(it) }
        // Clear the runnable reference
        autoScrollRunnable = null
    }

    /**
     * Start auto scroll for hero carousel
     */
    private fun startAutoScroll() {
        // Stop any existing auto scroll first
        stopAutoScroll()
        // Create new auto scroll runnable
        autoScrollRunnable = object : Runnable {
            override fun run() {
                if (realMovieCount > 1) {
                    // Calculate next position with circular logic
                    val nextPosition = (currentPosition + 1) % realMovieCount
                    
                    // If going from last to first, use instant scroll to avoid weird animation
                    if (currentPosition == realMovieCount - 1 && nextPosition == 0) {
                        heroRecycler.scrollToPosition(0) // Instant jump to first
                        currentPosition = 0
                    } else {
                        // For normal transitions, use smooth scroll
                        currentPosition = nextPosition
                        heroRecycler.smoothScrollToPosition(currentPosition)
                    }
                    
                    // Update page indicators to show current position
                    updatePageIndicators(currentPosition)
                    
                    // Schedule next auto scroll
                    handler.postDelayed(this, 3000) // 3 seconds delay between each auto scroll (time to view each movie)
                }
            }
        }
        // Start auto scroll with initial delay
        handler.postDelayed(autoScrollRunnable!!, 3000) // 3 seconds delay before starting auto scroll (initial delay when fragment loads)
    }

    /**
     * Pause fragment lifecycle
     * Stops auto scroll to save resources
     */
    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    /**
     * Resume fragment lifecycle
     * Checks for language changes and restarts auto scroll
     */
    override fun onResume() {
        super.onResume()
        // Check if language changed and reload data if needed
        viewModel.checkLanguageChange()
        // Reload all movies to get fresh data from background updates
        viewModel.reloadAllMovies()
        
        // Start auto scroll only if we have movies and not already running
        if (realMovieCount > 1 && autoScrollRunnable == null) {
            startAutoScroll()
        }
    }
}