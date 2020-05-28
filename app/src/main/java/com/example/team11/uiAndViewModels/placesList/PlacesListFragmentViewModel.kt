package com.example.team11.uiAndViewModels.placesList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Color
import com.example.team11.database.entity.Place
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.repository.PlaceRepository


class PlacesListFragmentViewModel(context: Context) : ViewModel(), ListViewModel {
    var places: LiveData<List<Place>>? = null
    lateinit var personalPreference: LiveData<List<PersonalPreference>>
    private var placeRepository: PlaceRepository? = null

    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance(context)
            places = placeRepository!!.getPlaces()
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }
    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    override fun changeCurrentPlace(place: Place){
        placeRepository?.changeCurrentPlace(place)
    }

    fun getForecasts(placesIn: List<Place>) = placeRepository!!.getNowForecastsList(placesIn)

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