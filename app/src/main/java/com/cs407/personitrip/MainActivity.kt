package com.cs407.personitrip

import ExploreFragment
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize settings button with dropdown functionality
        val settingsButton = findViewById<ImageButton>(R.id.settings_button)
        settingsButton.setOnClickListener { showSettingsMenu(it) }

        // Initialize TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        loadFragment(ExploreFragment()) // Default fragment

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadFragment(ExploreFragment())
                    1 -> loadFragment(MapFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // Load fragments dynamically
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Function to show the dropdown menu
    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.settings_menu, popup.menu) // Create a settings_menu.xml file for this

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_city -> {
                    val intent = Intent(this, EditCityActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_personality -> {
                    val intent = Intent(this, EditPreferencesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_saved -> {
                    val intent = Intent(this, EditItineraryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
