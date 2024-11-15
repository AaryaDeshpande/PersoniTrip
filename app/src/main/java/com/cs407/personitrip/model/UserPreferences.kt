package com.cs407.personitrip.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated ID
    val name: String, // Name of the preference, e.g., "Local Cuisine"
    val preferenceType: String // Type or category of the preference, e.g., "Food"
)
