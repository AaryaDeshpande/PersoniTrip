package com.cs407.personitrip

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs407.personitrip.databinding.ActivityEditPreferencesBinding

class EditPreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPreferencesBinding
    private var userPreferences = mutableSetOf<String>()
    private var userDislikes = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferencesFromSharedPrefs()

        setupPreferenceSwitches()

        binding.saveButton.setOnClickListener {
            savePreferencesToSharedPrefs()
            Toast.makeText(this, "Preferences updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadPreferencesFromSharedPrefs() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userPreferences = sharedPrefs.getStringSet("userPreferences", setOf())?.toMutableSet() ?: mutableSetOf()
        userDislikes = sharedPrefs.getStringSet("userDislikes", setOf())?.toMutableSet() ?: mutableSetOf()
    }

    private fun savePreferencesToSharedPrefs() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putStringSet("userPreferences", userPreferences)
        editor.putStringSet("userDislikes", userDislikes)
        editor.apply()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setupPreferenceSwitches() {
        val categories = listOf(
            "Local Cuisine",
            "Water Activities",
            "Outdoor Adventures",
            "Museums and Historical Sites",
            "Family-Friendly Activities",
            "Nightlife",
            "Shopping",
            "Architectural Sites",
            "Wildlife and Nature",
            "Adventure Sports",
            "Spa and Wellness",
            "Cultural Events"
        )

        categories.forEach { category ->
            val switch = Switch(this).apply {
                text = category
                isChecked = userPreferences.contains(category)
                setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                    if (isChecked) {
                        userPreferences.add(category)
                        userDislikes.remove(category) // Ensure consistency
                    } else {
                        userPreferences.remove(category)
                        userDislikes.add(category)
                    }
                }
            }
            binding.preferencesContainer.addView(switch)
        }
    }
}
