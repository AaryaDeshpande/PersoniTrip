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
import androidx.recyclerview.widget.RecyclerView
import com.cs407.personitrip.AttractionCardAdapter
import com.cs407.personitrip.AttractionCategory
import com.cs407.personitrip.BuildConfig
import com.cs407.personitrip.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


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
        recyclerView.adapter = AttractionCardAdapter(emptyList()) // Empty initially

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupObservers()

        checkLocationPermission()

        return root
    }

    private fun setupObservers() {
        exploreViewModel.attractions.observe(viewLifecycleOwner) { attractions ->
            recyclerView.adapter = AttractionCardAdapter(attractions)
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
        CoroutineScope(Dispatchers.IO).launch {
            val apiKey = BuildConfig.MAPS_API_KEY
            val urlStr =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=1500&type=tourist_attraction&key=$apiKey"

            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Success: Read the response
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val attractions = parseAttractionsFromJson(response)
                    val filteredAttractions = filterAttractionsByPreferences(attractions)

                    // Update the ViewModel
                    exploreViewModel.updateAttractions(filteredAttractions)
                } else {
                    // Log the error response
                    val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e("ExploreFragment", "API Error: $responseCode, $errorStream")
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch attractions", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Log any exceptions
                Log.e("ExploreFragment", "Error fetching attractions: ${e.message}", e)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to fetch attractions", Toast.LENGTH_SHORT).show()
                }
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
        val sharedPrefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userPreferences = sharedPrefs.getStringSet("userPreferences", setOf()) ?: setOf()
        val userDislikes = sharedPrefs.getStringSet("userDislikes", setOf()) ?: setOf()

        return attractions.filter { attraction ->
            userPreferences.any { like -> attraction.name.contains(like, ignoreCase = true) } &&
                    userDislikes.none { dislike -> attraction.name.contains(dislike, ignoreCase = true) }
        }
    }
}
