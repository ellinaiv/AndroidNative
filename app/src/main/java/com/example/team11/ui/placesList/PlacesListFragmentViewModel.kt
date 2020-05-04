package com.example.team11.ui.placesList

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.Place
import com.example.team11.Repository.PlaceRepository


class PlacesListFragmentViewModel(context: Context) : ViewModel() {
    var places: LiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null

    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance(context)
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

    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }
}