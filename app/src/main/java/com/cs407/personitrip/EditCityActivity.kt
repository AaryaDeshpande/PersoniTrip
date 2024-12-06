package com.cs407.personitrip

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.gms.common.api.Status
import android.content.Intent

class EditCityActivity : AppCompatActivity() {

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var selectedCity: String? = null
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_city)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDzhqjQ3_S38NEgpiaaostZvJ1rYvAP5j8")
        }

        startAutocomplete()
    }

    private fun startAutocomplete() {
        val fields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.NAME,
            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    selectedCity = place.name
                    selectedLatLng = place.latLng
                    Toast.makeText(this, "Selected: $selectedCity", Toast.LENGTH_SHORT).show()
                    saveCitySelection(selectedCity!!, selectedLatLng!!)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                }
                RESULT_CANCELED -> {
                    Toast.makeText(this, "Search canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCitySelection(cityName: String, location: LatLng) {
        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("selectedCityName", cityName)
        editor.putString("selectedCityLat", location.latitude.toString())
        editor.putString("selectedCityLng", location.longitude.toString())
        editor.apply()

        Toast.makeText(this, "City saved: $cityName", Toast.LENGTH_SHORT).show()
        finish()
    }
}
