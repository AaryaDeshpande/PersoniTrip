package com.cs407.personitrip

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.personitrip.databinding.ActivityEditItineraryBinding

class EditItineraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditItineraryBinding
    private var savedItinerary: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadItinerary()

        binding.itineraryRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ItineraryAdapter(savedItinerary.toList(), ::removeItineraryItem)
        binding.itineraryRecyclerView.adapter = adapter

        updateUI()
    }

    private fun loadItinerary() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        savedItinerary = sharedPrefs.getStringSet("savedItinerary", null)?.toMutableSet() ?: mutableSetOf()
    }

    private fun saveItinerary() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("savedItinerary", savedItinerary)
        editor.apply()
    }

    private fun updateUI() {
        if (savedItinerary.isEmpty()) {
            binding.itineraryRecyclerView.visibility = View.GONE
            binding.emptyItineraryMessage.visibility = View.VISIBLE
        } else {
            binding.itineraryRecyclerView.visibility = View.VISIBLE
            binding.emptyItineraryMessage.visibility = View.GONE
        }
    }

    private fun removeItineraryItem(item: String) {
        savedItinerary.remove(item)
        saveItinerary()
        (binding.itineraryRecyclerView.adapter as ItineraryAdapter).updateData(savedItinerary.toList())
        updateUI()
    }
}
