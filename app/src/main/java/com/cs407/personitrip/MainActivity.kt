package com.cs407.personitrip

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Main activity that handles location permission and fetching nearby attractions.
class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // Code to identify location permission request.
    private lateinit var userPreferences: Set<String> // Set to store user's liked preferences.
    private lateinit var userDislikes: Set<String> // Set to store user's disliked preferences.
    private lateinit var fusedLocationClient: FusedLocationProviderClient // Client to get current location

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

        // Load preferences from SharedPreferences
        loadPreferencesFromSharedPrefs()

        // Initialize the fused location provider client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if location permission is granted, and request it if not.
        checkLocationPermission()
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

    // Function to load preferences from SharedPreferences.
    private fun loadPreferencesFromSharedPrefs() {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userPreferences = sharedPrefs.getStringSet("userPreferences", setOf()) ?: setOf()
        userDislikes = sharedPrefs.getStringSet("userDislikes", setOf()) ?: setOf()
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
            getCurrentLocation()
        }
    }

    // Get the current location of the user
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                fetchNearbyAttractions(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
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
                getCurrentLocation()
            } else {
                // Notify user if permission is denied.
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchNearbyAttractions(latitude: Double, longitude: Double) {
        val apiKey = "AIzaSyANayAhfHcxjm34EIT8rwgHazhrzZxxRls"
        val urlStr =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=1500&type=tourist_attraction&key=$apiKey"

        Thread {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val attractions = parseAttractionsFromJson(response)

                runOnUiThread {
                    filterAttractionsByPreferences(attractions)
                }
            } catch (e: Exception) {
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
            val photosArray = result.optJSONArray("photos")
            val photoReference = photosArray?.getJSONObject(0)?.optString("photo_reference")
            attractions.add(AttractionCategory(name, R.drawable.default_attraction_image, photoReference))
        }
        return attractions
    }

    private fun getPhotoUrl(photoReference: String): String {
        val apiKey = "AIzaSyANayAhfHcxjm34EIT8rwgHazhrzZxxRls"
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=$apiKey"
    }



    // Filter attractions based on user preferences and dislikes.
    private fun filterAttractionsByPreferences(attractions: List<AttractionCategory>) {
        val relevantAttractions = attractions.filter { attraction ->
            userPreferences.any { like -> attraction.name.contains(like, ignoreCase = true) } &&
                    userDislikes.none { dislike -> attraction.name.contains(dislike, ignoreCase = true) }
        }
        displayAttractionsInCards(relevantAttractions)
    }

    // Display the filtered attractions in a card-based layout.
    private fun displayAttractionsInCards(attractions: List<AttractionCategory>) {
        // Here, display the attractions in your main activity UI.
        // You might use a RecyclerView or another card-based layout instead of launching a new activity.
        if (attractions.isNotEmpty()) {
            // Code to populate a RecyclerView or CardView goes here
            // Example: recyclerView.adapter = AttractionAdapter(attractions)
        } else {
            Toast.makeText(this, "No attractions match your preferences", Toast.LENGTH_SHORT).show()
        }
    }
}
