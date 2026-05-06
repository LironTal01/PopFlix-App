package com.example.popiflix.ui.favorites

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import com.google.android.material.snackbar.Snackbar
import com.example.popiflix.R
import com.example.popiflix.databinding.FragmentFavoritesBinding
import com.example.popiflix.ui.favorites.FavoriteViewModel

import dagger.hilt.android.AndroidEntryPoint

/**
 * FavoritesFragment displays the user's favorite movies in a scrollable list.
 * 
 * Features:
 * - Shows all favorite movies from local database
 * - Click to view movie details
 * - Swipe or long-press to remove from favorites
 * - Empty state when no favorites exist
 */
@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    // View binding for safe access to UI components
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing favorites data
    private val viewModel: FavoriteViewModel by viewModels()

    
    // Adapter for displaying favorites in RecyclerView
    private lateinit var adapter: FavoritesAdapter

    // Create the fragment view using view binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false) // create binding
        return binding.root // return root view
    }

    // Set up UI when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set texts in Hebrew/English from resources
        binding.emptyTitle.text = getString(R.string.fav_empty_title) // set empty title text
        binding.emptyDesc.text  = getString(R.string.fav_empty_desc) // set empty description text


        // Create adapter with click and share functionality
        adapter = FavoritesAdapter(
            onClick = { movie ->
                // Navigate to movie details when movie is clicked
                val action = FavoritesFragmentDirections.actionFavoritesToMovieDetail(movie.id) // create navigation action
                findNavController().navigate(action) // navigate to movie detail
            },
            onShare = { movie ->
                // Share movie details
                shareMovie(movie) // call share function
            }
        )
        
        // Set up RecyclerView with linear layout manager
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext()) // set vertical layout
        binding.recyclerViewFavorites.adapter = adapter // set adapter
        
        // Set up swipe to delete with red background
        setupSwipeToDelete() // enable swipe to delete functionality

        // Observe favorites list changes and update UI
        viewModel.favorites.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list) // update adapter with new list
            
            // Update favorites count
            updateFavoritesCount(list?.size ?: 0) // update count display
            
            // Show/hide empty state based on list content
            if (list.isNullOrEmpty()) { // if no favorites
                binding.recyclerViewFavorites.visibility = View.GONE // hide recycler view
                binding.emptyView.visibility = View.VISIBLE // show empty state
            } else { // if has favorites
                binding.recyclerViewFavorites.visibility = View.VISIBLE // show recycler view
                binding.emptyView.visibility = View.GONE // hide empty state
            }
        }
        

    }
    
    // Handle configuration changes (like language change)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload UI when configuration changes (e.g., language change)
        loadUI() // reload UI with new language
    }
    
    // Check for language changes when fragment resumes
    override fun onResume() {
        super.onResume()
        // Check if language changed and update saved items if needed
        viewModel.checkLanguageChange() // update saved items if language changed
    }
    
    // Load UI elements with current language
    private fun loadUI() {
        // Update text elements with current language
        binding.emptyTitle.text = getString(R.string.fav_empty_title) // set empty title
        binding.emptyDesc.text = getString(R.string.fav_empty_desc) // set empty description
    }
    
    // Update favorites count display
    private fun updateFavoritesCount(count: Int) {
        val countText = when (count) { // choose text based on count
            0 -> getString(R.string.favorites_count, 0) // zero favorites
            1 -> getString(R.string.favorite_count_single) // single favorite
            else -> getString(R.string.favorites_count, count) // multiple favorites
        }
        binding.favoritesCount.text = countText // set count text
    }
    

    
    // Set up swipe to delete functionality with red background and trash icon
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            // Define which movements are allowed
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // No drag and drop, only swipe left or right
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) // enable swipe left/right
            }

            // Disable drag and drop
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No drag and drop
            }

            // Handle swipe action
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition // get item position
                val movie = adapter.currentList[position] // get movie from adapter
                
                // Remove movie from favorites
                viewModel.remove(movie) // remove from database
                
                // Show undo snackbar
                Snackbar.make(
                    binding.root, // parent view
                    "${movie.getLocalizedTitle()} ${getString(R.string.removed_from_favorites)}", // message
                    Snackbar.LENGTH_LONG // show for long time
                ).setAction(getString(R.string.undo)) { // add undo button
                    // Add back to favorites
                    viewModel.addToFavorites(movie) // restore movie
                }.apply {
                    // Use theme color for undo button
                    setActionTextColor(ContextCompat.getColor(requireContext(), R.color.sort_button_text_color))
                    // Set text color for snackbar message
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.sort_button_text_color))
                    // Set background color for snackbar
                    view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_background_color))
                }.show() // display snackbar
            }

            // Draw custom swipe background and icon
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, // horizontal swipe distance
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView // get item view
                val icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete) // get trash icon
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2 // center icon vertically
                
                if (dX > 0) { // Swiping to the right
                    // Draw red background
                    val background = ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    background.setBounds(
                        itemView.left, // start from left edge
                        itemView.top,
                        itemView.left + dX.toInt(), // end at swipe distance
                        itemView.bottom
                    )
                    background.draw(c) // draw background
                    
                    // Draw trash icon
                    icon.setBounds(
                        itemView.left + iconMargin, // position icon
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + icon.intrinsicWidth,
                        itemView.top + iconMargin + icon.intrinsicHeight
                    )
                    icon.draw(c) // draw icon
                } else if (dX < 0) { // Swiping to the left
                    // Draw red background
                    val background = ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                    background.setBounds(
                        itemView.right + dX.toInt(), // start from right edge minus swipe distance
                        itemView.top,
                        itemView.right, // end at right edge
                        itemView.bottom
                    )
                    background.draw(c) // draw background
                    
                    // Draw trash icon
                    icon.setBounds(
                        itemView.right - iconMargin - icon.intrinsicWidth, // position icon from right
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.top + iconMargin + icon.intrinsicHeight
                    )
                    icon.draw(c) // draw icon
                }
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive) // call parent method
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavorites) // attach to recycler view
    }
    
    // Share movie details using Android's share intent
    private fun shareMovie(movie: com.example.popiflix.data.database.FavoriteMovie) {
        // Create personalized intro message with movie title
        val movieTitle = movie.getLocalizedTitle() // get localized title
        val introMessage = getString(R.string.share_movie_intro, movieTitle) // create intro message
        
        // Build detailed share text with movie information
        val shareText = buildString { // build share message
            // Add personalized intro message
            appendLine(introMessage) // add intro
            appendLine() // empty line
            
            // Add movie title with emoji for visual appeal
            appendLine("${getString(R.string.share_movie_emoji)} $movieTitle") // add title with emoji
            appendLine() // empty line
            
            // Add movie overview if available
            movie.overview?.let { overview -> // if has description
                appendLine("${getString(R.string.share_overview_emoji)} $overview") // add description
                appendLine() // empty line
            }
            
            // Add release date if available
            movie.releaseDate?.let { releaseDate -> // if has release date
                appendLine("${getString(R.string.share_date_emoji)} ${getString(R.string.release_date)}: $releaseDate") // add date
            }
            
            // Add rating if available
            movie.voteAverage?.let { rating -> // if has rating
                appendLine("${getString(R.string.share_rating_emoji)} ${getString(R.string.rating)}: ${String.format("%.1f", rating)}/10") // add rating
            }
            
            appendLine() // empty line
            // Add app signature
            appendLine(getString(R.string.share_app_signature)) // add app promotion
        }
        
        // Create Android share intent with text content
        val shareIntent = Intent().apply { // create share intent
            action = Intent.ACTION_SEND // set action
            type = getString(R.string.share_content_type) // set content type
            putExtra(Intent.EXTRA_TEXT, shareText) // add text to share
            putExtra(Intent.EXTRA_SUBJECT, movieTitle) // add subject
        }
        
        // Open system share chooser to let user select sharing app (WhatsApp, SMS, etc.)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_movie))) // open share chooser
    }

    // Clean up when view is destroyed
    override fun onDestroyView() {
        // Clean up binding to prevent memory leaks
        _binding = null // clear binding reference
        super.onDestroyView() // call parent method
    }
}
