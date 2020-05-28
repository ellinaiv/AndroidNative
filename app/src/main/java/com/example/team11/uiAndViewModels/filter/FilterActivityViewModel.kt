package com.example.team11.uiAndViewModels.filter

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.Repository.PlaceRepository

class FilterActivityViewModel(context: Context): ViewModel(){

    var personalPreferences: LiveData<List<PersonalPreference>>? = null

    private lateinit  var placeRepository: PlaceRepository
    /**
     * Setter verdier
     */
    init {
        if(personalPreferences == null){
            placeRepository = PlaceRepository.getInstance(context)
            personalPreferences = placeRepository.getPersonalPreferences()
        }
    }


    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    /**
     * Oppdaterer repository med den nye preferansen til brukeren
     */
    fun updatePersonalPreference(personalPreference: PersonalPreference){
        placeRepository.updatePersonalPreference(personalPreference)
    }
}