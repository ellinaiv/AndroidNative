package com.example.team11.ui.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.Place
import com.example.team11.Repository.PlaceRepository
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point

class MapFragmentViewModel(context: Context): ViewModel() {
    var places: LiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null

    /**
     * Setter verdier
     */
    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance(context)
            places = placeRepository!!.getPlaces()
        }
    }

    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en Feature verdi basert på lokasjonen til place
     */
    fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))!!

    fun changeCurrentPlace(place: Place){
        if(placeRepository != null){
            placeRepository!!.changeCurrentPlace(place)
        }else{
            Log.d("ViewModelTag", "finner ikke Placerepository ")
        }
    }


}