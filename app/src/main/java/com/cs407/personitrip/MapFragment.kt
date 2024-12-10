package com.cs407.personitrip

import ExploreViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {
    private val exploreViewModel: ExploreViewModel by activityViewModels()

    // Map variables for map values.
    private lateinit var mMap: GoogleMap
    private var initialLocationSet = false  // Add this flag
    // mDestinationLatLngs is going to be an ArrayList of Loc objects to plot on map.
    private var mDestinationLatLngs: ArrayList<Loc> = ArrayList()
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var mapView : MapView? = null
    get() = field

    /**
     * The Loc object is the location, with the LatLng and the Location Name.
     *
     * Params:
     * coordinates - LatLng of the location
     * name - what is the location called
     */
    data class Loc(val coordinates: LatLng, val name: String)

    /**
     * This is a callback that is available when the map is prepared.
     * It will parse the locations and maps added.
     *
     * TODO - NOTE for PLACEHOLDERs:
     * There are 3 placeholder locations:
     * 1. Picnic Point (43.0898, -89.4151)
     * 2. Tenney Park (43.0960, -89.3752)
     * 3. Camp Randall Stadium (43.0679, -89.4178)
     * All of the locations are my favorite locations in Madtown.
     *
     * The expectation is that this fragment will be passed a set of locations at some point.
     * TODO tags are added to complete that code.
     * Markers are then added with the basic location name and basic location info.
     *
     * Then it will zoom to the last location with 10f zoom.
     */
    private val callback = OnMapReadyCallback { googleMap ->
        Log.d("MapFragment", "onMapReady called")
        mMap = googleMap

        // Check if we already have locations
        val currentLocations = exploreViewModel.locations.value
        if (currentLocations != null && currentLocations.isNotEmpty()) {
            // If we have locations, show them immediately
            updateMapMarkers(currentLocations)
            initialLocationSet = true
        } else {
            // If no locations yet, show default pin
            Log.d("MapFragment", "No locations yet, showing default pin")
            val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val cityName = sharedPrefs.getString("selectedCityName", null)
            val cityLat = sharedPrefs.getString("selectedCityLat", null)?.toDoubleOrNull()
            val cityLng = sharedPrefs.getString("selectedCityLng", null)?.toDoubleOrNull()

            if (cityName != null && cityLat != null && cityLng != null) {
                updateLocation(Loc(LatLng(cityLat, cityLng), cityName))
            } else {
                val defaultLocation = Loc(LatLng(43.0731, -89.4012), "Madison, WI")
                updateLocation(defaultLocation)
            }
        }
    }

    private fun updateMapMarkers(locations: List<LatLng>) {
        if (::mMap.isInitialized) {
            mMap.clear()  // Clear existing markers

            locations.forEach { latLng ->
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }

            // Zoom to show all markers
            if (locations.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                locations.forEach { builder.include(it) }
                val bounds = builder.build()
                try {
                    val padding = 100
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    mMap.animateCamera(cameraUpdate)
                } catch (e: Exception) {
                    Log.e("MapFragment", "Error animating camera: ${e.message}")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MapFragment", "onCreateView")
        // start working on view
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // initialize mapview
        mapView = view.findViewById(R.id.map_frag_display)
        mapView?.onCreate(savedInstanceState)

        // Create async map
        mapView?.getMapAsync(callback)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exploreViewModel.locations.observe(viewLifecycleOwner) { locations ->
            Log.d("MapFragment", "Received locations: $locations")
            updateMapMarkers(locations)
            initialLocationSet = true
        }

//        exploreViewModel.locations.observe(viewLifecycleOwner) { locations ->
//            Log.d("MapFragment", "Received locations: $locations")
//            if (::mMap.isInitialized) {
//                // Clear existing markers
//                mMap.clear()
//
//                // Add new markers
//                locations.forEach { latLng ->
//                    mMap.addMarker(
//                        MarkerOptions()
//                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    )
//                }
//
//                // Zoom to fit all markers if there are any
//                if (locations.isNotEmpty()) {
//                    val builder = LatLngBounds.Builder()
//                    locations.forEach { builder.include(it) }
//                    val bounds = builder.build()
//
//                    try {
//                        val padding = 100 // pixels
//                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
//                        mMap.animateCamera(cameraUpdate)
//                    } catch (e: Exception) {
//                        Log.e("MapFragment", "Error animating camera: ${e.message}")
//                    }
//                } else {
//                    Log.e("MapFragment", "Map not initialized yet")
//                }
//            }
//        }
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map_frag_display) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
    }

    // Caution: do not modify.
    override fun onResume() {
        Log.d("MapFragment", "onResume")
        super.onResume()

//        val sharedPrefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        val cityName = sharedPrefs.getString("selectedCityName", null)
//        val cityLat = sharedPrefs.getString("selectedCityLat", null)?.toDoubleOrNull()
//        val cityLng = sharedPrefs.getString("selectedCityLng", null)?.toDoubleOrNull()
//
//        if (cityName != null && cityLat != null && cityLng != null) {
//            updateLocation(Loc(LatLng(cityLat, cityLng), cityName))
//        } else {
//            val defaultLocation = Loc(LatLng(43.0731, -89.4012), "Madison, WI")
//            updateLocation(defaultLocation)
//        }
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
    //update from settings
    fun updateLocation(newLocation: Loc) {
        mDestinationLatLngs.clear()
        mDestinationLatLngs.add(newLocation)

        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .position(newLocation.coordinates)
                .title(newLocation.name)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation.coordinates, 10f))
    }
}
