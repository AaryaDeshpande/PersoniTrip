import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.personitrip.AttractionCategory

class ExploreViewModel : ViewModel() {
    private val _attractions = MutableLiveData<List<AttractionCategory>>()
    val attractions: LiveData<List<AttractionCategory>> = _attractions

    fun updateAttractions(newAttractions: List<AttractionCategory>) {
        _attractions.value = newAttractions
    }
}
