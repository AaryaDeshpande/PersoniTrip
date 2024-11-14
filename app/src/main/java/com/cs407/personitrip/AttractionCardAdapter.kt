package com.cs407.personitrip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs407.personitrip.databinding.ItemAttractionCardBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager

// Adapter to manage and display attraction cards in a RecyclerView.
class AttractionCardAdapter(private val attractions: List<AttractionCategory>) :
    RecyclerView.Adapter<AttractionCardAdapter.AttractionViewHolder>() {

    // ViewHolder that holds the layout for each card in the RecyclerView.
    inner class AttractionViewHolder(val binding: ItemAttractionCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Called when RecyclerView needs a new ViewHolder of a particular type to represent an item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        // Inflate the layout for each attraction card using view binding.
        val binding = ItemAttractionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttractionViewHolder(binding)
    }

    // Binds data to each card (ViewHolder) at the specified position.
    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        // Set the title and image for each card using the data from AttractionCategory.
        holder.binding.attractionTitle.text = attraction.name
        holder.binding.attractionImage.setImageResource(attraction.imageResourceId)
    }

    // Returns the total number of items in the list of attractions.
    override fun getItemCount(): Int = attractions.size

    // Method to get the top attraction card, useful for swiping logic.
    // AttractionCardAdapter.kt
    fun getTopAttraction(manager: CardStackLayoutManager): AttractionCategory {
        return attractions[manager.topPosition] // Use manager's topPosition to get the current top card
    }

    // AttractionCardAdapter.kt
    fun getAttractionAt(position: Int): AttractionCategory {
        return attractions[position] // Return the card at the given position
    }
}
