package com.example.team11.ui.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.database.entity.Place
import com.example.team11.Repository.PlaceRepository
import com.example.team11.database.entity.WeatherForecastDb
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point

class MapFragmentViewModel(context: Context): ViewModel() {
    var places: LiveData<List<Place>>? = null
    var personalPreference: LiveData<List<PersonalPreference>>? = null
    private var placeRepository: PlaceRepository? = null

    /**
     * Setter verdier
     */
    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance(context)
            places = placeRepository!!.getPlaces()
            personalPreference = placeRepository!!.getPersonalPreferences()
        }
    }

    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    fun getNowForcast(place: Place): LiveData<List<WeatherForecastDb>>?{
        return placeRepository?.getNowForecastsList(listOf(place))
    }

    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en Feature verdi basert på lokasjonen til place
     */
    fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))!!

    /**
     * Endrer hvilken strand man vil undersøke nøyere
     */
    fun changeCurrentPlace(place: Place){
        placeRepository?.changeCurrentPlace(place)
    }

    /**
     * Sjekker om stedet er varmt, basert på brukeren sin preferance
     * @param place: stedet som man vil finne ut om er varmt
     * @return true hvis stedet er varmt, false hvis kaldt
     */
    fun isPlaceWarm(place: Place): Boolean{
//        val personalPreferenceValue = personalPreference!!.value!!
//       if(personalPreferenceValue.showBasedOnWater){
//        if(personalPreferenceValue.waterTempMid <= place.tempWater) return true
//       return false
//        }
        val personalPreferenceValue = personalPreference?.value?.get(0)?: return false
        if(personalPreferenceValue.showBasedOnWater){
            if(personalPreferenceValue.waterTempMid <= place.tempWater) return true
            return false
        }
        //TODO("This:")
        //if(personalPreferenceValue.airTempMid <= place.tempAir) return true
        return false
    }

    /**
     * Sjekker om stedet man er på skal ha en graa pin
     * @param place: stedet som man vil finne ut om skal være graa
     * @return true hvis graa, false ellers
     */
    fun isPinGray(place: Place): Boolean{
        val personalPreferenceValue = personalPreference?.value?.get(0)?: return true
        if(personalPreferenceValue.showBasedOnWater){
            if(place.tempWater == Int.MAX_VALUE) return true
            return false
        }
        //TODO("This: for tempAir")
        return false
    }

    /**
     * Sjekker om et sted skal ha rød eller blaa boolge
     * @param place: Stedet man vil sjekke
     */
    fun redWave(place: Place): Boolean{
        if(personalPreference!!.value!![0]!!.waterTempMid <= place.tempWater) return true
        return false
    }


}