import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.personitrip.AttractionCardAdapter
import com.cs407.personitrip.AttractionCategory
import com.cs407.personitrip.BuildConfig
import com.cs407.personitrip.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.libraries.places.api.net.FetchPhotoRequest


class ExploreFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private val exploreViewModel: ExploreViewModel by activityViewModels()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explore, container, false)
        recyclerView = root.findViewById(R.id.recyclerViewAttractions)

        // Set a LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = AttractionCardAdapter(emptyList(), true) // Empty initially

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupObservers()

        checkLocationPermission()

        return root
    }

    private fun setupObservers() {
        //val adapter = AttractionCardAdapter(emptyList())
        //recyclerView.adapter = adapter

        exploreViewModel.attractions.observe(viewLifecycleOwner) { attractions ->
            Log.d("ExploreFragment", "Observer triggered with attractions: ${attractions.size}")
            (recyclerView.adapter as? AttractionCardAdapter)?.updateData(attractions) // Update the adapter's data
        }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchNearbyAttractions()
        }
    }

    private fun fetchNearbyAttractions() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                fetchAttractionsFromGoogle(latitude, longitude)
            } else {
                Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAttractionsFromGoogle(latitude: Double, longitude: Double) {
        // Initialize PlacesClient
        val placesClient = Places.createClient(requireContext())

        // Create a RectangularBounds object for nearby places
        val locationBias = RectangularBounds.newInstance(
            LatLng(latitude - 0.01, longitude - 0.01), // Southwest corner
            LatLng(latitude + 0.01, longitude + 0.01)  // Northeast corner
        )

        // Define the fields you want to retrieve
        val request = FindCurrentPlaceRequest.newInstance(
            listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS)
        )

        // Call Places API to find current places
        placesClient.findCurrentPlace(request).addOnSuccessListener { response ->
            val attractions = mutableListOf<AttractionCategory>()

            for (placeLikelihood in response.placeLikelihoods) {
                val place = placeLikelihood.place
                val name = place.name
                val latLng = place.latLng

                val photoReference = place.photoMetadatas?.firstOrNull()?.toString()

                attractions.add(
                    AttractionCategory(
                        name = name ?: "Unknown",
                        photoReference = photoReference,
                        location = latLng
                    )
                )
            }

            // Filter attractions by preferences
            val filteredAttractions = filterAttractionsByPreferences(attractions)

            // Update the ViewModel
            exploreViewModel.updateAttractions(filteredAttractions)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Toast.makeText(requireContext(), "Failed to fetch places: $statusCode", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun parseAttractionsFromJson(json: String): List<AttractionCategory> {
        val attractions = mutableListOf<AttractionCategory>()
        val jsonObject = JSONObject(json)
        val resultsArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val result = resultsArray.getJSONObject(i)
            val name = result.getString("name")
            val photosArray = result.optJSONArray("photos")
            val photoReference = photosArray?.getJSONObject(0)?.optString("photo_reference")
            attractions.add(
                AttractionCategory(
                    name = name,
                    // imageResource = R.drawable.default_attraction_image,
                    photoReference = photoReference
                )
            )
        }
        return attractions
    }

    private fun filterAttractionsByPreferences(attractions: List<AttractionCategory>): List<AttractionCategory> {
        // Simply return the input list without applying any filtering.
        return attractions
    }

//    private fun filterAttractionsByPreferences(attractions: List<AttractionCategory>): List<AttractionCategory> {
//        val sharedPrefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        val userPreferences = sharedPrefs.getStringSet("userPreferences", setOf()) ?: setOf()
//        val userDislikes = sharedPrefs.getStringSet("userDislikes", setOf()) ?: setOf()
//
//        return attractions.filter { attraction ->
//            userPreferences.any { like -> attraction.name.contains(like, ignoreCase = true) } &&
//                    userDislikes.none { dislike -> attraction.name.contains(dislike, ignoreCase = true) }
//        }
//    }
}
