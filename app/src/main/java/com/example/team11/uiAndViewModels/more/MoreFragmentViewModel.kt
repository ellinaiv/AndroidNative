package com.example.team11.uiAndViewModels.more

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.Repository.PlaceRepository


class MoreFragmentViewModel(context: Context) : ViewModel() {
    private var placeRepository: PlaceRepository? = null
    var personalPreference: LiveData<List<PersonalPreference>>? = null

    init {
        if(personalPreference == null){
            placeRepository = PlaceRepository.getInstance(context)
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }
    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }
    fun updatePersonalPreference(pp: PersonalPreference) = placeRepository!!.updatePersonalPreference(pp)

    fun changeFalseData(newFalseData: Boolean){
        placeRepository!!.changeFalseData(newFalseData)
    }
}