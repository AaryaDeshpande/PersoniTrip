package com.cs407.personitrip

import android.app.Application
import androidx.room.Room
import com.cs407.personitrip.database.AppDatabase

class MyApplication : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Room database
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "personitrip-database" // Database name
        ).build()
    }
}
