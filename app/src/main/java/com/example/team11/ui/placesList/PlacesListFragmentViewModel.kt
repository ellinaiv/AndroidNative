package com.example.team11.ui.placesList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.team11.database.entity.Place
import com.example.team11.Repository.PlaceRepository


class PlacesListFragmentViewModel : ViewModel() {
    var places: MutableLiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null

    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance()
            places = placeRepository!!.getPlaces()
        }
    }

    fun changeCurrentPlace(place: Place){
        if(placeRepository != null){
            placeRepository!!.changeCurrentPlace(place)
        }else{
            Log.d("ViewModelTag", "finner ikke Placerepository ")
        }
    }
}