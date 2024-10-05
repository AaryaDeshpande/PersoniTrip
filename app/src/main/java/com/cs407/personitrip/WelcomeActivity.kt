package com.cs407.personitrip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class WelcomeActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000 // 3 seconds
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Delay before checking location permissions
        Handler().postDelayed({
            checkLocationPermission()
        }, SPLASH_DISPLAY_LENGTH.toLong())

    }

    // Check if location permission is granted, if not, request it
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed to main activity
            proceedToQuizActivity()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to main activity
                proceedToQuizActivity()
            } else {
                // Permission denied, handle accordingly or proceed without location
                proceedToQuizActivity()
            }
        }
    }

    // This will start the PersonalityQuizActivity with quiz cards
    private fun proceedToQuizActivity() {
        val quizIntent = Intent(this, PersonalityQuizActivity::class.java)
        startActivity(quizIntent)
        finish()
    }

//    private fun proceedToMainActivity() {
//        val mainIntent = Intent(this, MainActivity::class.java)
//        startActivity(mainIntent)
//        finish()
//    }
}
