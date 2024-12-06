package com.cs407.personitrip

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    // Map variables for map values.
    private lateinit var mMap: GoogleMap
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
        // Map is now available.
        mMap = googleMap

        // code to display markers
        /* TODO - NOTE for next programmer:
        I have added placeholders to locations. These are my favorite locations in Madtown.
        No, I do not plan on "gatekeeping" them - in fact, I would totally share them with others.
        However, they may not be other's favorite locations

        tl;dr - please remove the placeholders after verifying that other locations are added.

        Thanks.
        -dhdingwisc
         */
        // Picnic Point (43.0898, -89.4151)
        val locPicnic = Loc(LatLng(43.0898, -89.4151), "Picnic Point")
        mDestinationLatLngs.add(locPicnic)
        // Tenney Park (43.0960, -89.3752)
        mDestinationLatLngs.add(Loc(LatLng(43.0960, -89.3752), "Tenney Park"))
        // Camp Randall Stadium (43.0679, -89.4178)
        mDestinationLatLngs.add(Loc(LatLng(43.0679, -89.4178), "Camp Randall Stadium"))

        /* TODO - NOTE for next programmer:
        Please add necessary programming to add the locations from explore tab (generated from the
        planned implementation to find locations).
        While adding necessary programming, please be cautious that locations may need to be added
        to mDestinationLatLngs outside of the callback, but it has already been initialized as an
        ArrayList so that it is hopefully easier to go through them.

        Thanks.
        -dhdingwisc
         */

        // Loop through all locations in AL to add to the map.
        for (i in mDestinationLatLngs.indices) {
            mMap.addMarker(
                MarkerOptions()
                    .position(mDestinationLatLngs[i].coordinates)
                    .title(mDestinationLatLngs[i].name)
            )
        }
        // Zoom to last location.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestinationLatLngs[mDestinationLatLngs.size - 1].coordinates, 10f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map_frag_display) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()

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
