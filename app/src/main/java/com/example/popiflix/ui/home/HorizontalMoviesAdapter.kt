package com.example.popiflix.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.popiflix.R
import com.example.popiflix.data.models.Movie

/**
 * Adapter for horizontal movie carousel with posters
 */
class HorizontalMoviesAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<HorizontalMoviesAdapter.HorizontalMovieViewHolder>() {

    class HorizontalMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImage: ImageView = itemView.findViewById(R.id.posterImage)
    }

    /**
     * Create new view holder for horizontal movie item
     * Inflates the layout and returns view holder instance
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalMovieViewHolder {
        // Create view from layout XML file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horizontal_movie, parent, false)
        // Return new view holder with the created view
        return HorizontalMovieViewHolder(view)
    }

    /**
     * Bind movie data to view holder
     * Loads poster image and sets click animation
     */
    override fun onBindViewHolder(holder: HorizontalMovieViewHolder, position: Int) {
        // Get movie at current position
        val movie = movies[position]
        
        // Load poster image with optimizations
        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}") // Medium resolution poster
            .placeholder(R.drawable.ic_launcher_foreground) // Show while loading
            .error(R.drawable.ic_launcher_foreground) // Show if load fails
            .transform(RoundedCorners(20)) // Apply rounded corners
            .skipMemoryCache(false) // Allow memory caching
            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // Cache on disk
            .into(holder.posterImage)

        // Set click listener with inward press effect
        holder.itemView.setOnClickListener {
            // Scale down animation (inward press effect) - more pronounced
            holder.itemView.animate()
                .scaleX(0.85f) // Scale to 85% width
                .scaleY(0.85f) // Scale to 85% height
                .setDuration(120) // Animation duration in milliseconds
                .withEndAction {
                    // Scale back to normal
                    holder.itemView.animate()
                        .scaleX(1.0f) // Back to 100% width
                        .scaleY(1.0f) // Back to 100% height
                        .setDuration(120) // Animation duration
                        .start()
                }
                .start()
            
            // Call click callback with movie data
            onMovieClick(movie)
        }
    }

    /**
     * Get total number of items in adapter
     * Returns size of movies list
     */
    override fun getItemCount(): Int = movies.size

    /**
     * Update movies list and refresh adapter
     * Called when new data is available
     */
    fun updateMovies(newMovies: List<Movie>) {
        // Update movies list with new data
        movies = newMovies
        // Notify adapter that data changed
        notifyDataSetChanged()
    }
}
