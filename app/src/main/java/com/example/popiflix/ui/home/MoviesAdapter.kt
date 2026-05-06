package com.example.popiflix.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.popiflix.R
import com.example.popiflix.data.models.Movie

/**
 * Adapter for displaying movies in RecyclerView
 * Handles movie list display with click events
 */
class MoviesAdapter(
    private var items: List<Movie>,
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.VH>() {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        private val poster: ImageView = view.findViewById(R.id.poster)

        fun bind(movie: Movie) {
            val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            if (posterUrl != null) {
                val cornerRadius = poster.context.resources.getDimensionPixelSize(R.dimen.poster_corner_radius)
                Glide.with(poster.context)
                    .load(posterUrl)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadius)))
                    .into(poster)
            } else {
                poster.setImageResource(android.R.drawable.ic_menu_report_image)
            }
            view.setOnClickListener { onClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun submitList(new: List<Movie>) {
        items = new
        notifyDataSetChanged()
    }
}