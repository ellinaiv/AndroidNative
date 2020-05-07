package com.example.team11.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository

class FavoritesFragmentViewModel: ViewModel() {

    var favoritePlaces: MutableLiveData<List<Place>>? = null
    private var placeRepository: PlaceRepository? = null

    init {
        if(favoritePlaces == null){
            placeRepository = PlaceRepository.getInstance()
            favoritePlaces = placeRepository!!.getFavoritePlaces()
        }
    }
    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }

    /**
     * Sender beskjed til repository om at stedet man vil lese mer om skal endre seg.
     * Denne må kalles når man før man går inn i PlaceActivity
     * @param place: stedet man ønsker å dra til
     */
    fun changeCurrentPlace(place: Place){
        if(placeRepository != null){
            placeRepository!!.changeCurrentPlace(place)
        }else{
            Log.d("ViewModelTag", "finner ikke Placerepository ")
        }
    }
}