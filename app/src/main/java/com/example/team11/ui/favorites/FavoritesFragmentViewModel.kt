package com.example.team11.ui.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.PersonalPreference
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository

class FavoritesFragmentViewModel: ViewModel() {

    var favoritePlaces: MutableLiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null
    private var personalPreference: MutableLiveData<PersonalPreference>? = null

    init {
        if(favoritePlaces == null){

            placeRepository = PlaceRepository.getInstance()
            favoritePlaces = placeRepository!!.getFavoritePlaces()
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }

    /**
     * Sender beskjed til repository om at stedet man vil lese mer om skal endre seg.
     * Denne må kalles når man før man går inn i PlaceActivity
     * @param place: stedet man ønsker å dra til
     */
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