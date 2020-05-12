package com.example.team11.ui.filter

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.PersonalPreference
import com.example.team11.Repository.PlaceRepository
import kotlin.coroutines.coroutineContext

class FilterActivityViewModel(context: Context): ViewModel(){

    var personalPreferences: MutableLiveData<PersonalPreference>? = null

    private var placeRepository: PlaceRepository? = null
    /**
     * Setter verdier
     */
    init {
        if(personalPreferences == null){
            placeRepository = PlaceRepository.getInstance(context)
            personalPreferences = placeRepository!!.getPersonalPreferences()
        }
    }


    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    /**
     * Reseter personlige preferanser til det vi har valgt som standar
     */
    fun resetPersonalPreference(){
        placeRepository!!.updatePersonalPreference(PersonalPreference())
    }

    /**
     * Oppdaterer repository med den nye preferansen til brukeren
     */
    fun updatePersonalPreference(personalPreference: PersonalPreference){
        placeRepository!!.updatePersonalPreference(personalPreference)
    }
}