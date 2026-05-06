package com.example.popiflix.ui.watchlist

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.popiflix.R
import com.example.popiflix.databinding.FragmentWatchlistBinding
import dagger.hilt.android.AndroidEntryPoint

// WatchlistFragment displays the user's watchlist movies in a scrollable list
@AndroidEntryPoint
class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null // binding reference
    private val binding get() = _binding!! // get binding safely

    private val viewModel: WatchlistViewModel by viewModels() // get view model
    private lateinit var adapter: WatchlistAdapter // adapter for recycler view

    // Create the fragment view using view binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false) // create binding
        return binding.root // return root view
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
        binding.emptyTitle.text = getString(R.string.watch_empty_title) // set empty title
        binding.emptyDesc.text = getString(R.string.watch_empty_desc) // set empty description
    }

    // Set up UI when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up empty state text
        binding.emptyTitle.text = getString(R.string.watch_empty_title) // set empty title
        binding.emptyDesc.text = getString(R.string.watch_empty_desc) // set empty description

        // Create adapter with click functionality and edit functionality
        adapter = WatchlistAdapter(
            onClick = { movie ->
                // Navigate to movie details when movie is clicked
                val action = WatchlistFragmentDirections.actionWatchlistToMovieDetail(movie.id) // create navigation action
                findNavController().navigate(action) // navigate to movie detail
            },
            onEditClick = { movie ->
                // Show edit tags dialog when edit button is clicked
                showEditTagsDialog(movie) // show edit dialog
            }
        )

        binding.recyclerViewWatchlist.layoutManager = LinearLayoutManager(requireContext()) // set vertical layout
        binding.recyclerViewWatchlist.adapter = adapter // set adapter
        
        // Set up swipe to delete with red background
        setupSwipeToDelete() // enable swipe to delete functionality
        
        // Set up search functionality
        setupSearch() // enable search feature
        
        // Set up sort functionality
        setupSort() // enable sort feature
        
        // Update current sort text based on saved preference
        updateCurrentSortText() // show current sort option

        // Observe watchlist changes and update UI
        viewModel.watchlist.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list) // update adapter with new list
            if (list.isNullOrEmpty()) { // if no movies
                binding.recyclerViewWatchlist.visibility = View.GONE // hide recycler view
                binding.emptyView.visibility = View.VISIBLE // show empty state
            } else { // if has movies
                binding.recyclerViewWatchlist.visibility = View.VISIBLE // show recycler view
                binding.emptyView.visibility = View.GONE // hide empty state
            }
        }
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
                
                // Remove movie from watchlist
                viewModel.remove(movie) // remove from database
                
                // Show undo snackbar
                Snackbar.make(
                    binding.root, // parent view
                    "${movie.getLocalizedTitle()} ${getString(R.string.removed_from_watchlist)}", // message
                    Snackbar.LENGTH_LONG // show for long time
                ).setAction(getString(R.string.undo)) { // add undo button
                    // Add back to watchlist
                    viewModel.addToWatchlist(movie) // restore movie
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
        
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewWatchlist) // attach to recycler view
    }
    
    /**
     * Set up search functionality for filtering by notes
     */
    private fun setupSearch() {
        val searchEditText = binding.searchEditText
        
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s?.toString() ?: ""
                viewModel.searchByNotes(query)
            }
        })
    }
    
    /**
     * Set up sort functionality with dialog
     */
    private fun setupSort() {
        val sortButton = binding.sortButton
        
        sortButton.setOnClickListener {
            showSortDialog()
        }
    }
    
    /**
     * Update current sort text based on saved preference
     */
    private fun updateCurrentSortText() {
        val sortType = viewModel.getCurrentSortType()
        val textRes = when (sortType) {
            "date_added" -> R.string.sorted_by_date_added
            "alphabetical" -> R.string.sorted_by_alphabetical
            "release_date" -> R.string.sorted_by_release_date
            "rating" -> R.string.sorted_by_rating
            else -> R.string.sorted_by_date_added
        }
        binding.currentSortText.text = getString(textRes)
    }
    
    /**
     * Show dialog to edit movie tags
     */
    private fun showEditTagsDialog(movie: com.example.popiflix.data.database.WatchlistMovie) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_to_watchlist, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editTextNotes)
        
        // Pre-fill with current notes
        editText.setText(movie.userNotes)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.edit_tags_dialog_title))
            .setMessage(getString(R.string.edit_tags_dialog_message))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.add_to_watchlist_dialog_add)) { _, _ ->
                val newNotes = editText.text?.toString()?.trim()
                viewModel.updateMovieNotes(movie.id, if (newNotes.isNullOrEmpty()) null else newNotes)
            }
            .setNegativeButton(getString(R.string.add_to_watchlist_dialog_cancel), null)
            .show()
    }
    
    /**
     * Show sort options dialog
     */
    private fun showSortDialog() {
        val sortOptions = arrayOf(
            getString(R.string.sort_by_date_added),
            getString(R.string.sort_by_alphabetical),
            getString(R.string.sort_by_release_date),
            getString(R.string.sort_by_rating)
        )
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sort))
            .setItems(sortOptions) { _, which ->
                when (which) {
                    0 -> {
                        viewModel.sortByDateAdded()
                        binding.currentSortText.text = getString(R.string.sorted_by_date_added)
                    }
                    1 -> {
                        viewModel.sortByAlphabetical()
                        binding.currentSortText.text = getString(R.string.sorted_by_alphabetical)
                    }
                    2 -> {
                        viewModel.sortByReleaseDate()
                        binding.currentSortText.text = getString(R.string.sorted_by_release_date)
                    }
                    3 -> {
                        viewModel.sortByRating()
                        binding.currentSortText.text = getString(R.string.sorted_by_rating)
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
