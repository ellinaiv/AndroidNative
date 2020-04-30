package com.example.team11.ui.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.PersonalPreference
import com.example.team11.Repository.PlaceRepository

class FilterActivityViewModel: ViewModel(){

    var personalPreferences: MutableLiveData<PersonalPreference>? = null

    private var placeRepository: PlaceRepository? = null
    /**
     * Setter verdier
     */
    init {
        if(personalPreferences == null){
            placeRepository = PlaceRepository.getInstance()
            personalPreferences = placeRepository!!.getPersonalPreferences()
        }
    }


    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }

    fun resetPersonalPreference(){
        placeRepository!!.updatePersonalPreference(PersonalPreference())
    }

    fun updatePersonalPreference(personalPreference: PersonalPreference){
        placeRepository!!.updatePersonalPreference(personalPreference)
    }
}