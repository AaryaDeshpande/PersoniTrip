package com.cs407.personitrip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cs407.personitrip.databinding.ItemAttractionCardBinding

class AttractionCardAdapter(private val attractions: List<AttractionCategory>) :
    RecyclerView.Adapter<AttractionCardAdapter.AttractionViewHolder>() {

    inner class AttractionViewHolder(val binding: ItemAttractionCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val binding = ItemAttractionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttractionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.binding.attractionTitle.text = attraction.name
        holder.binding.attractionImage.setImageResource(attraction.imageResourceId)
    }

    override fun getItemCount(): Int = attractions.size

    fun getTopAttraction(): AttractionCategory = attractions[0] // Return the top card
}
