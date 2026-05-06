package com.example.popiflix.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.popiflix.R
import com.example.popiflix.data.models.PopularSearch

/**
 * Adapter for displaying popular searches in RecyclerView
 * Handles popular searches list with click functionality
 */
class PopularSearchesAdapter(
    private val onSearchClick: (String) -> Unit,
    private val onClearClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<PopularSearchesAdapter.PopularSearchViewHolder>() {

    private var searches = listOf<PopularSearch>()

    fun submitList(searches: List<PopularSearch>) {
        this.searches = searches
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_search, parent, false)
        return PopularSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularSearchViewHolder, position: Int) {
        holder.bind(searches[position])
    }

    override fun getItemCount(): Int = searches.size

    inner class PopularSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchText: TextView = itemView.findViewById(R.id.searchText)
        private val searchEmoji: TextView = itemView.findViewById(R.id.searchEmoji)
        private val clearButton: ImageView = itemView.findViewById(R.id.clearButton)

        fun bind(search: PopularSearch) {
            searchText.text = search.query
            searchEmoji.text = search.emoji ?: ""
            
            // Show clear button only for recent searches (with clock emoji)
            val isRecentSearch = search.emoji == "🕒"
            clearButton.visibility = if (isRecentSearch && onClearClick != null) View.VISIBLE else View.GONE
            
            // Set click listener for search
            itemView.setOnClickListener {
                onSearchClick(search.query)
            }
            
            // Set click listener for clear button
            clearButton.setOnClickListener {
                onClearClick?.invoke(search.query)
            }
        }
    }
}
