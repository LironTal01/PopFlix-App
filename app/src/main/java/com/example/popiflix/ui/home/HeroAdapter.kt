package com.example.popiflix.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.popiflix.R
import com.example.popiflix.data.models.Movie

/**
 * Adapter for hero carousel with backdrop images and movie details
 */
class HeroAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {
    
    /**
     * Create infinite scroll by duplicating movies list
     * This allows smooth scrolling without reaching the end
     */
    private fun getInfiniteMovies(): List<Movie> {
        // Return empty list if no movies, otherwise triple the list for infinite scroll
        return if (movies.isEmpty()) emptyList() else movies + movies + movies
    }

    class HeroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backdropImage: ImageView = itemView.findViewById(R.id.backdropImage)
        val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        val movieYear: TextView = itemView.findViewById(R.id.movieYear)
        val movieGenres: TextView = itemView.findViewById(R.id.movieGenres)
    }

    /**
     * Create new view holder for hero movie item
     * Inflates the layout and returns view holder instance
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        // Create view from layout XML file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hero_movie, parent, false)
        // Return new view holder with the created view
        return HeroViewHolder(view)
    }

    /**
     * Bind movie data to view holder
     * Loads image, sets text content and click listener
     */
    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        // Get movie from infinite list (handles circular scrolling)
        val movie = getInfiniteMovies()[position]
        
        // Load backdrop image with rounded corners
        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w1280${movie.backdropPath}") // High resolution backdrop
            .placeholder(R.drawable.hero_image_background) // Show while loading
            .error(R.drawable.hero_image_background) // Show if load fails
            .transform(RoundedCorners(24)) // Apply rounded corners
            .into(holder.backdropImage)

        // Set movie details
        holder.movieTitle.text = movie.title
        // Extract year from release date (first 4 characters)
        holder.movieYear.text = movie.releaseDate?.substring(0, 4) ?: ""
        
        // Set genres (placeholder until API integration)
        holder.movieGenres.text = "Action, Drama" // TODO: Get real genres from API

        // Set click listener on entire item
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    /**
     * Get total number of items in adapter
     * Returns size of infinite movies list
     */
    override fun getItemCount(): Int = getInfiniteMovies().size

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
    
    /**
     * Get real position in original movies list
     * Used for infinite scroll calculations
     */
    fun getRealPosition(position: Int): Int {
        // Calculate real position using modulo for infinite scroll
        return position % movies.size
    }
}
