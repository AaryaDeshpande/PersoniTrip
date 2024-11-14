package com.cs407.personitrip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// Activity to display the splash screen and handle location permission.
class WelcomeActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000 // Splash screen duration in milliseconds (3 seconds).
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001 // Request code for location permission.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Display the splash screen for a set amount of time and then check location permission.
        Handler().postDelayed({
            checkLocationPermission()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    // Function to check for location permissions.
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if it hasn't been granted.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // If permission is granted, proceed to quiz activity.
            proceedToQuizActivity()
        }
    }

    // Handle the result of the permission request.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, proceed to quiz activity.
                proceedToQuizActivity()
            } else {
                // If permission is denied, proceed without location.
                proceedToQuizActivity()
            }
        }
    }

    // Function to start the PersonalityQuizActivity after splash screen.
    private fun proceedToQuizActivity() {
        val quizIntent = Intent(this, PersonalityQuizActivity::class.java)
        startActivity(quizIntent)
        finish() // Close the current activity so it cannot be returned to.
    }
}
