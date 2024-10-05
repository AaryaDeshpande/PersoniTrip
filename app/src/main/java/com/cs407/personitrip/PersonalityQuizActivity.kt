package com.cs407.personitrip

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cs407.personitrip.databinding.ActivityPersonalityQuizBinding
import com.yuyakaido.android.cardstackview.*

class PersonalityQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalityQuizBinding
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: AttractionCardAdapter
    private val userPreferences = mutableListOf<String>() // Store liked attractions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up view binding
        binding = ActivityPersonalityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize CardStackView and its manager
        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {
                val topAttraction = adapter.getTopAttraction()
                if (direction == Direction.Right) {
                    userPreferences.add(topAttraction.name) // Add liked attractions
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        // Set up RecyclerView (CardStackView) adapter
        adapter = AttractionCardAdapter(getAttractionCategories())
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
    }

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
}
