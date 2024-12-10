package com.cs407.personitrip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cs407.personitrip.databinding.ActivityPersonalityQuizBinding
import com.yuyakaido.android.cardstackview.*

// Activity to display personality quiz cards for swiping.
class PersonalityQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalityQuizBinding // View binding for this activity.
    private lateinit var manager: CardStackLayoutManager // Manages the layout of card stack.
    private lateinit var adapter: AttractionCardAdapter // Adapter to bind the attractions to the card stack.
    private val userPreferences = mutableListOf<String>() // List to store user's liked attractions.
    private val userDislikes = mutableListOf<String>() // List to store user's disliked attractions.

    // :)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding for easy access to UI elements.
        binding = ActivityPersonalityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the CardStackView and its manager.
        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction) {
                val swipedAttraction = adapter.getAttractionAt(manager.topPosition - 1) // Use position of the swiped card

                if (direction == Direction.Right) {
                    // User swiped right (liked the attraction).
                    userPreferences.add(swipedAttraction.name)
                } else if (direction == Direction.Left) {
                    // User swiped left (disliked the attraction).
                    userDislikes.add(swipedAttraction.name)
                }

                // Check if this was the last card
                if (manager.topPosition == adapter.itemCount) { // Last card check
                    savePreferencesToSharedPrefs() // Save preferences
                    fetchNearbyAttractions() // Move to MainActivity
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        // Set up the adapter for the CardStackView.
        adapter = AttractionCardAdapter(getAttractionCategories(), false)
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
    }

    // Function to get the initial list of attraction categories to be displayed.
    private fun getAttractionCategories(): List<AttractionCategory> {
        return listOf(
            AttractionCategory("Local Cuisine", R.drawable.local_cuisine),
            AttractionCategory("Water Activities", R.drawable.water_activities),
            AttractionCategory("Outdoor Adventures", R.drawable.outdoor_adventures),
            AttractionCategory("Museums and Historical Sites", R.drawable.museums),
            AttractionCategory("Family-Friendly Activities", R.drawable.family_friendly),
            AttractionCategory("Nightlife", R.drawable.nightlife),
            AttractionCategory("Shopping", R.drawable.shopping),
            AttractionCategory("Architectural Sites", R.drawable.architectural_sites),
            AttractionCategory("Wildlife and Nature", R.drawable.wildlife),
            AttractionCategory("Adventure Sports", R.drawable.adventure_sports),
            AttractionCategory("Spa and Wellness", R.drawable.spa),
            AttractionCategory("Cultural Events", R.drawable.cultural_events)
        )
    }

    // Move to MainActivity to fetch nearby attractions after user swipes through cards.
    override fun onDestroy() {
        super.onDestroy()
        savePreferencesToSharedPrefs() // Save likes/dislikes to SharedPreferences
        fetchNearbyAttractions() // Proceed to the main screen
    }

    // Function to save preferences to SharedPreferences.
    private fun savePreferencesToSharedPrefs() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("userPreferences", userPreferences.toSet()) // Save liked categories
        editor.putStringSet("userDislikes", userDislikes.toSet()) // Save disliked categories
        editor.apply() // Commit the changes
    }

    // Function to fetch nearby attractions after swiping.
    private fun fetchNearbyAttractions() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }
}
