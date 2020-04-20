package com.example.team11.ui.fragmentList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.team11.Place
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
}