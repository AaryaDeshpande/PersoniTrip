import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.personitrip.AttractionCategory
import com.google.android.gms.maps.model.LatLng

class ExploreViewModel : ViewModel() {
    private val _attractions = MutableLiveData<List<AttractionCategory>>()
    val attractions: LiveData<List<AttractionCategory>> = _attractions

    // Add new LiveData for locations
    private val _locations = MutableLiveData<List<LatLng>>()
    val locations: LiveData<List<LatLng>> = _locations

    fun updateAttractions(newAttractions: List<AttractionCategory>) {
        Log.d("ExploreViewModel", "HIIII")
        _attractions.value = newAttractions
        // Update locations whenever attractions are updated
        _locations.value = newAttractions.mapNotNull { it.location }
        Log.d("ExploreViewModel", "Locations: ${_locations.value}")
    }
}
