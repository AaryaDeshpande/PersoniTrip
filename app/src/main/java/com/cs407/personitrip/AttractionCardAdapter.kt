package com.cs407.personitrip

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cs407.personitrip.databinding.ItemAttractionCardBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.bumptech.glide.Glide


// Adapter to manage and display attraction cards in a RecyclerView.
class AttractionCardAdapter(private var attractions: List<AttractionCategory>,
                            private val isExploreFragment: Boolean) :
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
        Log.d("AttractionCardAdapter", "Binding data for position $position: ${attraction.name}")
        holder.binding.attractionTitle.text = attraction.name

        // Load image using Glide
        val context = holder.binding.root.context
        val photoUrl = attraction.photoReference?.let { getPhotoUrl(it) }

        Log.d("AttractionCardAdapter", "Loading image for ${attraction.name}: $photoUrl")

        Glide.with(context)
            .load(photoUrl ?: attraction.imageResourceId) // Use default image if no photoReference
            .placeholder(R.drawable.default_attraction_image)
            .into(holder.binding.attractionImage)

        if (isExploreFragment) {
            holder.binding.addToItineraryButton.visibility = View.VISIBLE
            holder.binding.addToItineraryButton.setOnClickListener {
                addAttractionToItinerary(attraction.name, context)
            }
        } else {
            holder.binding.addToItineraryButton.visibility = View.GONE
        }
    }

    private fun getPhotoUrl(photoReference: String): String {
        val apiKey = BuildConfig.MAPS_API_KEY // TODO add API key
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=$apiKey"
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

    fun updateData(newAttractions: List<AttractionCategory>) {
        Log.d("AttractionCardAdapter", "Updating data with ${newAttractions.size} attractions")
        attractions = newAttractions
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
    //add to itinerary in sharedpreferences
    private fun addAttractionToItinerary(attractionName: String, context: Context) {
        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedItinerary = sharedPrefs.getStringSet("savedItinerary", mutableSetOf())?.toMutableSet()
        savedItinerary?.add(attractionName)

        sharedPrefs.edit().putStringSet("savedItinerary", savedItinerary).apply()

        Toast.makeText(context, "$attractionName added to itinerary", Toast.LENGTH_SHORT).show()
    }
}
