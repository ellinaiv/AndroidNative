package com.example.team11.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository


class PlacesListActivityViewModel: ViewModel() {

    var places: MutableLiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null

    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance()
            places = placeRepository!!.getPlaces()
        }
    }
    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }
}