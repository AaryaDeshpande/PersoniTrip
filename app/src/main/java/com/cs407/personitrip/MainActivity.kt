package com.cs407.personitrip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Main activity that handles location permission and fetching nearby attractions.
class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // Code to identify location permission request.
    private lateinit var userPreferences: List<String> // List to store user's preferences.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if location permission is granted, and request it if not.
        checkLocationPermission()
    }

    // Function to check location permissions.
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Fetch nearby attractions if permission is granted.
            fetchNearbyAttractions()
        }
    }

    // Handles the result of the permission request.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission granted, fetch nearby attractions.
                fetchNearbyAttractions()
            } else {
                // Notify user if permission is denied.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fetch nearby attractions using the Google Places API.
    private fun fetchNearbyAttractions() {
        val latitude = "43.0731"
        val longitude = "-89.4012"
        val apiKey = "YOUR_GOOGLE_API_KEY"
        val urlStr =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=1500&type=tourist_attraction&key=$apiKey"

        // Run the network request on a separate thread to prevent blocking the UI.
        Thread {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Read the response.
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val attractions = parseAttractionsFromJson(response)

                // Update the UI with the filtered attractions.
                runOnUiThread {
                    filterAttractionsByPreferences(attractions)
                }
            } catch (e: Exception) {
                // Handle any exceptions and notify the user.
                runOnUiThread {
                    Toast.makeText(this, "Failed to fetch attractions", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    // Parse the JSON response from Google Places API to extract attraction information.
    private fun parseAttractionsFromJson(json: String): List<AttractionCategory> {
        val attractions = mutableListOf<AttractionCategory>()
        val jsonObject = JSONObject(json)
        val resultsArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val result = resultsArray.getJSONObject(i)
            val name = result.getString("name")
            val imageResourceId = R.drawable.default_attraction_image // Placeholder image.
            attractions.add(AttractionCategory(name, imageResourceId))
        }
        return attractions
    }

    // Filter attractions based on user preferences.
    private fun filterAttractionsByPreferences(attractions: List<AttractionCategory>) {
        val relevantAttractions = attractions.filter { attraction ->
            userPreferences.any { like -> attraction.name.contains(like, ignoreCase = true) }
        }
        displayAttractionsInCards(relevantAttractions)
    }

    // Display the filtered attractions in a card-based layout.
    private fun displayAttractionsInCards(attractions: List<AttractionCategory>) {
        val quizIntent = Intent(this, PersonalityQuizActivity::class.java)
        quizIntent.putParcelableArrayListExtra("filtered_attractions", ArrayList(attractions))
        startActivity(quizIntent)
    }
}
