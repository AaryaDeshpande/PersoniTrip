package com.cs407.personitrip.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cs407.personitrip.dao.UserPreferencesDao
import com.cs407.personitrip.model.UserPreferences

@Database(entities = [UserPreferences::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}
