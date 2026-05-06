package com.example.popiflix.ui.detail

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.popiflix.R
import com.example.popiflix.data.models.CreditsResponse
import com.example.popiflix.data.models.MovieDetails
import com.example.popiflix.data.repository.FavoriteRepository
import com.example.popiflix.data.repository.WatchlistRepository
import com.example.popiflix.data.database.WatchlistMovie
import com.example.popiflix.ui.home.HomeViewModel

import com.example.popiflix.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

// Movie detail screen fragment
@AndroidEntryPoint
class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    private val args: MovieDetailFragmentArgs by navArgs() // get navigation arguments
    private val viewModel: HomeViewModel by viewModels() // get view model

    @Inject lateinit var favoriteRepository: FavoriteRepository // inject favorite repository
    @Inject lateinit var watchlistRepository: WatchlistRepository // inject watchlist repository

    // UI elements
    private lateinit var imagePoster: ImageView // movie poster image
    private lateinit var textTitle: TextView // movie title
    private lateinit var textMeta: TextView // movie metadata
    private lateinit var textRating: TextView // movie rating
    private lateinit var textGenres: TextView // movie genres
    private lateinit var textOverview: TextView // movie overview
    private lateinit var textCast: TextView // cast information
    private lateinit var textDirector: TextView // director information

    private lateinit var buttonAddToFavorites: Button // add to favorites button
    private lateinit var buttonAddToWatchlist: Button // add to watchlist button
    private lateinit var buttonRateMovie: FrameLayout // rate movie button

    // Set up UI when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar with back arrow → navigateUp()
        val toolbar: Toolbar = view.findViewById(R.id.toolbar) // get toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar) // set as action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true) // show back button
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // hide title
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() } // handle back click

        initializeViews(view) // initialize all views

        setupFavoriteButton() // set up favorites button
        setupWatchlistButton() // set up watchlist button
        setupRateMovieButton() // set up rating button
        observeMovieDetails() // observe movie data

        viewModel.getMovieDetails(args.movieId) // load movie details
        viewModel.getMovieCredits(args.movieId) // load movie credits
        println("Loading current rating for movie: ${args.movieId}") // debug log
        viewModel.getCurrentMovieRating(args.movieId) // load current rating

        // System back → navigateUp()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp() // go back
                }
            }
        )
    }

    // Handle configuration changes (like language change)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        loadUI() // reload UI with new language
    }

    // Load UI elements with current language
    private fun loadUI() {
        // Update localized UI if needed
    }

    // Initialize all view references
    private fun initializeViews(view: View) {
        imagePoster = view.findViewById(R.id.imagePoster) // get poster image
        textTitle = view.findViewById(R.id.textTitle) // get title text
        textMeta = view.findViewById(R.id.textMeta) // get metadata text
        textRating = view.findViewById(R.id.textRating) // get rating text
        textGenres = view.findViewById(R.id.textGenres) // get genres text
        textOverview = view.findViewById(R.id.textOverview) // get overview text
        textCast = view.findViewById(R.id.textCast) // get cast text
        textDirector = view.findViewById(R.id.textDirector) // get director text

        buttonAddToFavorites = view.findViewById(R.id.buttonAddToFavorites) // get favorites button
        buttonAddToWatchlist = view.findViewById(R.id.buttonAddToWatchlist) // get watchlist button
        buttonRateMovie = view.findViewById(R.id.buttonRateMovie) // get rate button
    }

    // Observe movie data changes
    private fun observeMovieDetails() {
        viewModel.movieDetails.observe(viewLifecycleOwner) { details ->
            details?.let { // if has details
                displayMovieDetails(it) // show movie details
                checkFavoriteStatus(it.id) // check if in favorites
                checkWatchlistStatus(it.id) // check if in watchlist
            }
        }
        viewModel.movieCredits.observe(viewLifecycleOwner) { credits ->
            credits?.let { displayMovieCredits(it) } // show credits if available
        }
        
        // Observe rating status
        viewModel.ratingStatus.observe(viewLifecycleOwner) { status ->
            status?.let { handleRatingStatus(it) } // handle rating status
        }
        
        // Observe current rating
        viewModel.currentRating.observe(viewLifecycleOwner) { rating ->
            rating?.let { // if has rating
                println("Current rating loaded: $it") // debug log
                // Update the rate movie dialog if it's open
                updateRateMovieDialog(rating) // update dialog with current rating
            }
        }

    }



    // Set up favorites button click listener
    private fun setupFavoriteButton() {
        buttonAddToFavorites.setOnClickListener {
            viewModel.movieDetails.value?.let { toggleFavorite(it) } // toggle favorite status
        }
    }

    // Set up watchlist button click listener
    private fun setupWatchlistButton() {
        buttonAddToWatchlist.setOnClickListener {
            viewModel.movieDetails.value?.let { toggleWatchlist(it) } // toggle watchlist status
        }
    }

    // Set up rating button click listener
    private fun setupRateMovieButton() {
        buttonRateMovie.setOnClickListener {
            viewModel.movieDetails.value?.let { showRateMovieDialog(it.id) } // show rating dialog
        }
    }


    // Display movie details in UI
    private fun displayMovieDetails(movie: MovieDetails) {
        val posterUrl = "${Constants.BASE_IMAGE_URL}${movie.posterPath}" // build poster URL
        // Get corner radius from resources
        val cornerRadius = resources.getDimensionPixelSize(R.dimen.poster_corner_radius) // get corner radius
        
        Glide.with(imagePoster.context) // get Glide instance
            .load(posterUrl) // load poster image
            .placeholder(R.drawable.ic_launcher_background) // show placeholder while loading
            .error(R.drawable.ic_launcher_background) // show error image if load fails
            .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))) // apply rounded corners
            .into(imagePoster) // load into image view

        textTitle.text = movie.title // set movie title

        val year = movie.releaseDate?.split("-")?.get(0) ?: getString(R.string.na) // get release year
        val runtime = movie.runtime?.let { getString(R.string.minutes_short, it) } ?: getString(R.string.na) // get runtime
        textMeta.text = "$year • $runtime" // set metadata text

        textRating.text = if (movie.voteAverage != 0.0) // if has rating
            "${getString(R.string.rating_out_of_ten, movie.voteAverage)} ⭐" // show rating
        else
            "${getString(R.string.na)} ⭐" // show N/A

        val genresText = if (movie.genres.isNotEmpty()) { // if has genres
            movie.genres.joinToString(" | ") { it.name } // join genre names
        } else getString(R.string.no_genres_available) // show no genres message
        textGenres.text = genresText // set genres text

        textOverview.text = movie.overview ?: getString(R.string.no_overview) // set overview or no overview message
        textCast.text = "${getString(R.string.starring)}: ${getString(R.string.loading)}" // show loading for cast
        textDirector.text = "${getString(R.string.director)}: ${getString(R.string.loading)}" // show loading for director
    }

    // Display movie credits (cast and crew)
    private fun displayMovieCredits(credits: CreditsResponse) {
        val mainCast = credits.cast.take(5) // get first 5 cast members
        val castText = if (mainCast.isNotEmpty()) { // if has cast
            val actorNames = mainCast.joinToString(", ") { it.name } // join actor names
            "${getString(R.string.starring)}: $actorNames" // show starring text
        } else "${getString(R.string.starring)}: ${getString(R.string.no_cast_available)}" // show no cast message
        textCast.text = castText // set cast text

        val director = credits.crew.find { it.job.equals(getString(R.string.job_director), ignoreCase = true) } // find director
        val directorText = if (director != null) // if has director
            "${getString(R.string.director)}: ${director.name}" // show director name
        else
            "${getString(R.string.director)}: ${getString(R.string.no_director_available)}" // show no director message
        textDirector.text = directorText // set director text
    }

    // Check if movie is in favorites
    private fun checkFavoriteStatus(movieId: Int) {
        CoroutineScope(Dispatchers.Main).launch { // launch coroutine on main thread
            val isFavorite = withContext(Dispatchers.IO) { favoriteRepository.isFavorite(movieId) } // check in background
            updateFavoriteButton(isFavorite) // update button UI
        }
    }

    // Update favorites button appearance
    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) { // if in favorites
            buttonAddToFavorites.text = getString(R.string.remove_from_favorites) // set remove text
            buttonAddToFavorites.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(getString(R.string.color_red))) // set red background
            
            // Animation to change to minus icon
            animateFavoriteIconChange(R.drawable.ic_minus) // change to minus icon
        } else { // if not in favorites
            buttonAddToFavorites.text = getString(R.string.add_to_favorites) // set add text
            buttonAddToFavorites.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(getString(R.string.color_pink))) // set pink background
            
            // Animation to change to like icon
            animateFavoriteIconChange(R.drawable.ic_like) // change to like icon
        }
    }
    
    // Change favorites button icon
    private fun animateFavoriteIconChange(newIconRes: Int) {
        // Change icon directly without animation
        buttonAddToFavorites.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), newIconRes), null, null, null // set new icon
        )
    }

    // Toggle movie favorite status
    private fun toggleFavorite(movieDetails: MovieDetails) {
        CoroutineScope(Dispatchers.Main).launch { // launch coroutine on main thread
            val isCurrentlyFavorite = withContext(Dispatchers.IO) { // check in background
                favoriteRepository.isFavorite(movieDetails.id) // check if currently favorite
            }
            if (isCurrentlyFavorite) { // if currently favorite
                withContext(Dispatchers.IO) { favoriteRepository.removeFromFavorites(movieDetails.id) } // remove from favorites
                Toast.makeText(requireContext(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show() // show message
                updateFavoriteButton(false) // update button to add state
            } else { // if not currently favorite
                withContext(Dispatchers.IO) { favoriteRepository.addToFavorites(movieDetails) } // add to favorites
                Toast.makeText(requireContext(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show() // show message
                updateFavoriteButton(true) // update button to remove state
            }
        }
    }

    // Check if movie is in watchlist
    private fun checkWatchlistStatus(movieId: Int) {
        CoroutineScope(Dispatchers.Main).launch { // launch coroutine on main thread
            val isInWatchlist = withContext(Dispatchers.IO) { watchlistRepository.isInWatchlist(movieId) } // check in background
            updateWatchlistButton(isInWatchlist) // update button UI
        }
    }

    // Update watchlist button appearance
    private fun updateWatchlistButton(isInWatchlist: Boolean) {
        if (isInWatchlist) { // if in watchlist
            buttonAddToWatchlist.text = getString(R.string.remove_from_watchlist) // set remove text
            buttonAddToWatchlist.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(getString(R.string.color_red))) // set red background
            
            // Animation to change to minus icon
            animateIconChange(R.drawable.ic_minus) // change to minus icon
        } else { // if not in watchlist
            buttonAddToWatchlist.text = getString(R.string.add_to_watchlist) // set add text
            buttonAddToWatchlist.backgroundTintList =
                android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(getString(R.string.color_blue))) // set blue background
            
            // Animation to change to plus icon
            animateIconChange(R.drawable.ic_plus) // change to plus icon
        }
    }
    
    // Change watchlist button icon
    private fun animateIconChange(newIconRes: Int) {
        // Change icon directly without animation
        buttonAddToWatchlist.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), newIconRes), null, null, null // set new icon
        )
    }
    


    // Toggle movie watchlist status
    private fun toggleWatchlist(movieDetails: MovieDetails) {
        CoroutineScope(Dispatchers.Main).launch { // launch coroutine on main thread
            val isCurrentlyInWatchlist = withContext(Dispatchers.IO) { // check in background
                watchlistRepository.isInWatchlist(movieDetails.id) // check if currently in watchlist
            }

            if (isCurrentlyInWatchlist) { // if currently in watchlist
                withContext(Dispatchers.IO) { watchlistRepository.removeFromWatchlist(movieDetails.id) } // remove from watchlist
                Toast.makeText(requireContext(), getString(R.string.watchlist_removed), Toast.LENGTH_SHORT).show() // show message
                updateWatchlistButton(false) // update button to add state
            } else { // if not currently in watchlist
                showAddToWatchlistDialog(movieDetails) // show add dialog
            }
        }
    }

    // Show dialog to add movie to watchlist with notes
    private fun showAddToWatchlistDialog(movieDetails: MovieDetails) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_to_watchlist, null) // inflate dialog layout
        val editTextNotes = dialogView.findViewById<EditText>(R.id.editTextNotes) // get notes input

        val dialog = AlertDialog.Builder(requireContext()) // create dialog builder
            .setTitle(getString(R.string.add_to_watchlist_dialog_title)) // set title
            .setMessage(getString(R.string.add_to_watchlist_dialog_message)) // set message
            .setView(dialogView) // set custom view
            .setPositiveButton(getString(R.string.add_to_watchlist_dialog_add)) { _, _ -> // add button
                val notes = editTextNotes.text.toString().trim() // get notes text
                addToWatchlistWithNotes(movieDetails, if (notes.isNotEmpty()) notes else null) // add with notes
            }
            .setNegativeButton(getString(R.string.add_to_watchlist_dialog_cancel), null) // cancel button
            .create() // create dialog

        dialog.show() // show dialog
    }

    // Add movie to watchlist with optional notes
    private fun addToWatchlistWithNotes(movieDetails: MovieDetails, notes: String?) {
        CoroutineScope(Dispatchers.Main).launch { // launch coroutine on main thread
            withContext(Dispatchers.IO) { // run in background
                watchlistRepository.addToWatchlist(movieDetails, userNotes = notes) // add to watchlist
            }
            Toast.makeText(requireContext(), getString(R.string.watchlist_added), Toast.LENGTH_SHORT).show() // show message
            updateWatchlistButton(true) // update button to remove state
        }
    }



    // Rating dialog variables
    private var currentDialog: AlertDialog? = null // current rating dialog
    private var currentSeekBar: SeekBar? = null // current seek bar
    private var currentTextRating: TextView? = null // current rating text
    private var currentRatingValue = 0.0 // current rating value

    // Show rating dialog for the movie
    private fun showRateMovieDialog(movieId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rate_movie, null) // inflate dialog layout
        val seekBar = dialogView.findViewById<SeekBar>(R.id.ratingSeekBar) // get seek bar
        val textRating = dialogView.findViewById<TextView>(R.id.textRatingValue) // get rating text
        
        // Store references for later updates
        currentSeekBar = seekBar // store seek bar reference
        currentTextRating = textRating // store text reference
        
        // Set initial rating from ViewModel if available
        val existingRating = viewModel.currentRating.value // get existing rating
        if (existingRating != null) { // if has existing rating
            currentRatingValue = existingRating // set current rating
            seekBar.progress = (existingRating * 2).toInt() // set seek bar progress
            textRating.text = "${String.format("%.1f", existingRating)}/10" // set rating text
        } else { // if no existing rating
            currentRatingValue = 0.0 // set to 0
            seekBar.progress = 0 // set seek bar to 0
            textRating.text = "0.0/10" // set text to 0
        }
        
        // Set up SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentRatingValue = progress / 2.0 // Convert to 0.5-10.0 scale
                textRating.text = "${String.format("%.1f", currentRatingValue)}/10" // update text
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // not used
            override fun onStopTrackingTouch(seekBar: SeekBar?) {} // not used
        })

        val movieTitle = viewModel.movieDetails.value?.title ?: "Movie" // get movie title
        currentDialog = AlertDialog.Builder(requireContext()) // create dialog builder
            .setTitle(getString(R.string.rate_movie_dialog_title, movieTitle)) // set title
            .setView(dialogView) // set custom view
            .setPositiveButton(getString(R.string.rate_movie_dialog_rate)) { _, _ -> // rate button
                if (currentRatingValue > 0) { // if has rating
                    rateMovie(movieId, currentRatingValue) // submit rating
                }
            }
            .setNeutralButton(getString(R.string.rate_movie_dialog_remove)) { _, _ -> // remove button
                deleteMovieRating(movieId) // delete rating
            }
            .setNegativeButton(getString(R.string.rate_movie_dialog_cancel), null) // cancel button
            .create() // create dialog

        currentDialog?.show() // show dialog
    }
    
    // Update rating dialog with new rating
    private fun updateRateMovieDialog(rating: Double) {
        currentSeekBar?.let { seekBar -> // if has seek bar
            currentTextRating?.let { textRating -> // if has text
                currentRatingValue = rating // set current rating
                seekBar.progress = (rating * 2).toInt() // set seek bar progress
                textRating.text = "${String.format("%.1f", rating)}/10" // set rating text
            }
        }
    }

    // Rate the movie
    private fun rateMovie(movieId: Int, rating: Double) {
        viewModel.rateMovie(movieId, rating) // submit rating to view model
        // Don't show success message here - wait for response
    }
    
    // Handle rating status response
    private fun handleRatingStatus(status: String) {
        when {
            status.startsWith("success:") -> { // if success
                val rating = status.substringAfter("success:") // get rating value
                Toast.makeText(requireContext(), getString(R.string.rating_saved, rating), Toast.LENGTH_LONG).show() // show success message
            }
            status.startsWith("error:") -> { // if error
                val error = status.substringAfter("error:") // get error message
                Toast.makeText(requireContext(), getString(R.string.rating_error, error), Toast.LENGTH_LONG).show() // show error message
            }
        }
    }

    // Delete movie rating
    private fun deleteMovieRating(movieId: Int) {
        viewModel.deleteMovieRating(movieId) // delete rating from view model
        Toast.makeText(requireContext(), getString(R.string.rating_removed), Toast.LENGTH_SHORT).show() // show message
    }


    // Clean up when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearMovieDetails() // clear movie data
    }
}
