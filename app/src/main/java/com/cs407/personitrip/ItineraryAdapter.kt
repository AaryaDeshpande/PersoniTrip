package com.cs407.personitrip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItineraryAdapter(
    private var activities: List<String>,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itinerary_item, parent, false)
        return ItineraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val activity = activities[position]
        holder.activityName.text = activity
        holder.removeButton.setOnClickListener { onRemoveClick(activity) }
    }

    override fun getItemCount(): Int = activities.size

    fun updateData(newActivities: List<String>) {
        activities = newActivities
        notifyDataSetChanged()
    }

    class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.activityName)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }
}
