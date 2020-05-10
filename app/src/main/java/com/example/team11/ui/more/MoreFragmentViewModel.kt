package com.example.team11.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.team11.Repository.PlaceRepository


class MoreFragmentViewModel : ViewModel() {
    private var placeRepository: PlaceRepository? = null
    var falseData:MutableLiveData<Boolean>? = null

    init {
        if(falseData == null){
            placeRepository = PlaceRepository.getInstance()
            falseData = placeRepository!!.getFalseData()
        }
    }

    fun changeFalseData(newFalseData: Boolean){
        placeRepository!!.changeFalseData(newFalseData)
    }
}