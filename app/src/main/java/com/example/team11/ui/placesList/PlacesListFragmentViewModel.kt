package com.example.team11.ui.placesList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.team11.PersonalPreference
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository


class PlacesListFragmentViewModel : ViewModel() {
    var places: MutableLiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null
    private var personalPreference: MutableLiveData<PersonalPreference>? = null

    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance()
            places = placeRepository!!.getPlaces()
            personalPreference = placeRepository!!.getPersonalPreferences()

        }
    }

    fun changeCurrentPlace(place: Place){
        placeRepository?.changeCurrentPlace(place)
    }

    /**
     * Sjekker om et sted skal ha rød eller blaa boolge
     * @param place: Stedet man vil sjekke
     */
    fun redWave(place: Place): Boolean{
        if(personalPreference!!.value!!.waterTempMid <= place.tempWater) return true
        return false
    }
}