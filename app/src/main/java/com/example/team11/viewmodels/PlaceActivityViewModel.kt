package com.example.team11.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository
import com.example.team11.Transporatation
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point

class PlaceActivityViewModel: ViewModel() {

    var place: MutableLiveData<Place>? = null
    private var placeRepository: PlaceRepository? = null

    /**
     * Setter verdier
     */
    init {
        if(place == null){
            placeRepository = PlaceRepository.getInstance()
            place = placeRepository!!.getCurrentPlace()
        }
    }


    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }

    fun changeWayOfTransportation(way: Transporatation){
        placeRepository!!.changeWayOfTransportation(way)
    }

    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en Feature verdi basert på lokasjonen til place
     */
    fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))!!
}