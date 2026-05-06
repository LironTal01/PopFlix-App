package com.example.popiflix.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.popiflix.data.database.FavoriteMovie
import com.example.popiflix.databinding.ItemFavoriteBinding

// Adapter for displaying favorite movies in RecyclerView
class FavoritesAdapter(
    private val onClick: (FavoriteMovie) -> Unit = {},  // Callback for movie click (navigate to details)
    private val onShare: (FavoriteMovie) -> Unit = {}   // Callback for share button click
) : ListAdapter<FavoriteMovie, FavoritesAdapter.VH>(DIFF) {

    // DiffUtil callback to optimize list updates and animations
    object DIFF : DiffUtil.ItemCallback<FavoriteMovie>() {
        override fun areItemsTheSame(a: FavoriteMovie, b: FavoriteMovie) = a.id == b.id // compare by ID
        override fun areContentsTheSame(a: FavoriteMovie, b: FavoriteMovie) = a == b // compare all fields
    }

    // ViewHolder class that holds the binding for item_favorite.xml layout
    inner class VH(val b: ItemFavoriteBinding) : RecyclerView.ViewHolder(b.root) // view holder with binding

    // Create new view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false) // inflate layout
        return VH(b) // return view holder
    }

    // Bind data to view holder
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) // get movie at position
        
        // Set movie title using localized version
        holder.b.title.text = item.getLocalizedTitle() // set localized title
        
        // Extract and display release year from date string
        holder.b.releaseYear.text = item.releaseDate?.take(4) ?: "N/A" // get first 4 characters (year)
        
        // Format and display rating with star emoji
        val rating = item.voteAverage?.let { // if has rating
            "⭐ ${String.format("%.1f", it)}" // format with 1 decimal place
        } ?: "⭐ N/A" // show N/A if no rating
        holder.b.rating.text = rating // set rating text
        
        // Display movie genres if available, otherwise show empty string
        val genresText = item.genres?.let { genres -> // if has genres
            // Split comma-separated genres and join with pipe separator
            genres.split(",").joinToString(" | ") { it.trim() } // split and format
        } ?: "" // empty string if no genres
        holder.b.genre.text = genresText // set genre text

        // Build TMDB image URL - prefer backdrop (horizontal) over poster
        val url = item.backdropPath?.let { path -> // if has backdrop
            // Use backdrop image if available (w500 for good quality)
            if (path.startsWith("http")) path else "https://image.tmdb.org/t/p/w500$path" // build URL
        } ?: item.posterPath?.let { path -> // fallback to poster
            // Fallback to poster image if no backdrop available (w342 for good quality)
            if (path.startsWith("http")) path else "https://image.tmdb.org/t/p/w342$path" // build URL
        }

        // Load movie image with Glide library
        val cornerRadius = holder.b.poster.context.resources.getDimensionPixelSize(com.example.popiflix.R.dimen.poster_corner_radius) // get corner radius
        Glide.with(holder.b.poster.context) // get Glide instance
            .load(url) // load image URL
            .placeholder(android.R.drawable.ic_menu_report_image)  // Show placeholder while loading
            .error(android.R.drawable.ic_menu_report_image)  // Show error image if load fails
            .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))) // apply rounded corners
            .into(holder.b.poster) // load into image view

        // Set click listeners for different actions
        holder.itemView.setOnClickListener { onClick(item) }  // Navigate to movie details
        holder.b.shareButton.setOnClickListener { onShare(item) }  // Open share dialog
    }

}
