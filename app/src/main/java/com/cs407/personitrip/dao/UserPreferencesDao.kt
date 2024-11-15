package com.cs407.personitrip.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cs407.personitrip.model.UserPreferences

@Dao
interface UserPreferencesDao {

    @Insert
    suspend fun insertPreference(preference: UserPreferences)

    @Query("SELECT * FROM user_preferences")
    suspend fun getAllPreferences(): List<UserPreferences>

    @Query("DELETE FROM user_preferences")
    suspend fun clearPreferences()
}
