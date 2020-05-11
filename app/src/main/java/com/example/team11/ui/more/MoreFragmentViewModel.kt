package com.example.team11.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.team11.PersonalPreference
import com.example.team11.Repository.PlaceRepository


class MoreFragmentViewModel : ViewModel() {
    private var placeRepository: PlaceRepository? = null
    var personalPreference:MutableLiveData<PersonalPreference>? = null

    init {
        if(personalPreference == null){
            placeRepository = PlaceRepository.getInstance()
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }

    fun changeFalseData(newFalseData: Boolean){
        placeRepository!!.changeFalseData(newFalseData)
    }
}