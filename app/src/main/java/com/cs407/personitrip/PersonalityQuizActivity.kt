package com.cs407.personitrip

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding for easy access to UI elements.
        binding = ActivityPersonalityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the CardStackView and its manager.
        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction) {
                val topAttraction = adapter.getTopAttraction()
                if (direction == Direction.Right) {
                    // User swiped right (liked the attraction).
                    userPreferences.add(topAttraction.name)
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        // Set up the adapter for the CardStackView.
        adapter = AttractionCardAdapter(getAttractionCategories())
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
        fetchNearbyAttractions()
    }

    // Function to fetch nearby attractions after swiping.
    private fun fetchNearbyAttractions() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putStringArrayListExtra("userPreferences", ArrayList(userPreferences))
        startActivity(mainIntent)
    }
}