package com.example.addresssearchapp.presentation.adapter

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.addresssearchapp.R
import com.example.addresssearchapp.data.model.LocationItem
import java.util.*

class LocationAdapter(
    private val onItemClick: (LocationItem) -> Unit
) : ListAdapter<LocationItem, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {

    private var searchQuery: String = ""

    fun updateSearchQuery(query: String) {
        searchQuery = query.lowercase(Locale.getDefault())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position), searchQuery, onItemClick)
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addressText: TextView = itemView.findViewById(R.id.tv_address)

        fun bind(
            location: LocationItem,
            searchQuery: String,
            onItemClick: (LocationItem) -> Unit
        ) {
            // Highlight search keywords
            val spannableAddress = highlightSearchKeywords(location.address, searchQuery)
            addressText.text = spannableAddress

            itemView.setOnClickListener {
                onItemClick(location)
            }
        }

        private fun highlightSearchKeywords(text: String, query: String): SpannableString {
            val spannable = SpannableString(text)

            if (query.isNotEmpty()) {
                val lowerText = text.lowercase(Locale.getDefault())
                val lowerQuery = query.lowercase(Locale.getDefault())

                var startIndex = 0
                while (startIndex < lowerText.length) {
                    val index = lowerText.indexOf(lowerQuery, startIndex)
                    if (index != -1) {
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            index,
                            index + query.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        startIndex = index + query.length
                    } else {
                        break
                    }
                }
            }

            return spannable
        }
    }
}

class LocationDiffCallback : DiffUtil.ItemCallback<LocationItem>() {
    override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem == newItem
    }
}