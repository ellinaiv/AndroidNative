package com.example.team11.ui.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Color
import com.example.team11.database.entity.Place
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.Repository.PlaceRepository
import com.example.team11.ui.placesList.ListViewModel

class FavoritesFragmentViewModel(context: Context): ViewModel(), ListViewModel {

    var favoritePlaces: LiveData<List<Place>>? = null
    private lateinit var placeRepository: PlaceRepository
    lateinit var personalPreference: LiveData<List<PersonalPreference>>

    init {
        if(favoritePlaces == null){
            placeRepository = PlaceRepository.getInstance(context)
            favoritePlaces = placeRepository.getFavoritePlaces()
            personalPreference = placeRepository.getPersonalPreferences()
        }
    }
    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }
    fun getForecasts(placesIn: List<Place>) = placeRepository.getNowForecastsList(placesIn)

    /**
     * Sender beskjed til repository om at stedet man vil lese mer om skal endre seg.
     * Denne må kalles når man før man går inn i PlaceActivity
     * @param place: stedet man ønsker å dra til
     */
    override fun changeCurrentPlace(place: Place){
        placeRepository.changeCurrentPlace(place)
    }

    /**
     * Sjekker om et sted skal ha graa, rood eller blaa boolge
     * @param place: Stedet man vil sjekke
     * @return fargen boolgen skal veare
     */
    override fun colorWave(place: Place): Color {
        if(place.tempWater == Int.MAX_VALUE) return Color.GRAY
        val waterTemp = personalPreference.value?.get(0)?.waterTempMid ?: return Color.GRAY
        if(waterTemp <= place.tempWater) return Color.RED
        return Color.BLUE
    }
}