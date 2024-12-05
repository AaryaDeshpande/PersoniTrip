package com.cs407.personitrip

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cs407.personitrip.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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

    private val callback = OnMapReadyCallback { googleMap ->
        // Map is now available.
        mMap = googleMap

        // code to display markers
        //TODO this is a placeholder to add random locations around Madison... that I like... and have been to
        // Picnic Point (43.0898, -89.4151)
        val locPicnic = Loc(LatLng(43.0898, -89.4151), "Picnic Point")
        mDestinationLatLngs.add(locPicnic)
        // Tenney Park (43.0960, -89.3752)
        mDestinationLatLngs.add(Loc(LatLng(43.0960, -89.3752), "Tenney Park"))
        // Camp Randall Stadium (43.0679, -89.4178)
        mDestinationLatLngs.add(Loc(LatLng(43.0679, -89.4178), "Camp Randall Stadium"))

        // TODO However, code is necessary to add Loc items from selected locations from previous activities.

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

        android.util.Log.d("MapFragment", "onCreateView crash") //todo rm
        // initialize mapview
        mapView = view.findViewById(R.id.map_frag_display)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(callback)

        checkShowUp() //todo rm

        // Create async map


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map_frag_display) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
        checkShowUp()
    }

    /**
     * Test that this fragment shows up.
     */
    fun checkShowUp() {
        // Use a toast.
        Toast.makeText(getActivity(), "MapFragment", Toast.LENGTH_SHORT).show();
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
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
}
