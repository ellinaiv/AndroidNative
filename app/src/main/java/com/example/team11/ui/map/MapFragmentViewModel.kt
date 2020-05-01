package com.example.team11.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.team11.PersonalPreference
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point

class MapFragmentViewModel: ViewModel() {
    var places: MutableLiveData<List<Place>>? = null
    var personalPreference: MutableLiveData<PersonalPreference>? = null
    private var placeRepository: PlaceRepository? = null

    /**
     * Setter verdier
     */
    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance()
            places = placeRepository!!.getPlaces()
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }

    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }

    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en Feature verdi basert på lokasjonen til place
     */
    fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))!!

    /**
     * Endrer hvilken strand man vil undersøke nøyere
     */
    fun changeCurrentPlace(place: Place){
        if(placeRepository != null){
            placeRepository!!.changeCurrentPlace(place)
        }else{
            Log.d("ViewModelTag", "finner ikke Placerepository ")
        }
    }

    /**
     * Sjekker om stedet er varmt, basert på brukeren sin preferance
     * @param place: stedet som man vil finne ut om er varmt
     * @return true hvis stedet er varmt, false hvis kaldt
     */
    fun isPlaceWarm(place: Place): Boolean{
        val personalPreferenceValue = personalPreference!!.value!!
        if(personalPreferenceValue.showBasedOnWater){
            if(personalPreferenceValue.waterTempMid <= place.tempWater) return true
            return false
        }
        if(personalPreferenceValue.airTempMid <= place.tempAir) return true
        return false
    }


}