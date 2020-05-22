package com.example.team11.ui.map

import android.content.Context
import android.util.Log
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
    lateinit var personalPreference: LiveData<List<PersonalPreference>>
    private lateinit var placeRepository: PlaceRepository
    val hasInternet = MutableLiveData<Boolean>()
    var listOfNowForecast = emptyList<WeatherForecastDb>()

    /**
     * Setter verdier
     */
    init {
        if(places == null){
            placeRepository = PlaceRepository.getInstance(context)
            places = placeRepository.getPlaces()
            personalPreference = placeRepository.getPersonalPreferences()
            hasInternet.value = false
        }
    }

    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
        }
    }

    fun getNowForecast(places: List<Place>): LiveData<List<WeatherForecastDb>>?{
        return placeRepository.getNowForecastsList(places)
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
        placeRepository.changeCurrentPlace(place)
    }

    fun getPlaceNowForecast(place: Place): WeatherForecastDb?{
        val filterList = listOfNowForecast.filter { it.placeId == place.id }
        if(filterList.isEmpty()) return null
        return filterList[0]
    }

    /**
     * Sjekker om stedet er varmt, basert på brukeren sin preferance
     * @param place: stedet som man vil finne ut om er varmt
     * @return true hvis stedet er varmt, false hvis kaldt
     */
    fun isPlaceWarm(place: Place): Boolean{
        val personalPreferenceValue = personalPreference.value?.get(0)?: return false
        if(personalPreferenceValue.showBasedOnWater){
            if(personalPreferenceValue.waterTempMid <= place.tempWater) return true
            return false
        }
        Log.d("isPlaceWarm", "BEASET PÅ AIR")
        val tempAir = getPlaceNowForecast(place) ?: return false
        Log.d("isPlaceWarm", tempAir.tempAir.toString() + " <> " + personalPreferenceValue.airTempMid.toString())
        Log.d("aitTemp", personalPreferenceValue.airTempMid.toString())
        if(personalPreferenceValue.airTempMid <= tempAir.tempAir) return true
        return false
    }

    /**
     * Sjekker om stedet man er på skal ha en graa pin
     * @param place: stedet som man vil finne ut om skal være graa
     * @return true hvis graa, false ellers
     */
    fun isPinGray(place: Place): Boolean{
        val personalPreferenceValue = personalPreference.value?.get(0)?: return true
        if(personalPreferenceValue.showBasedOnWater){
            if(place.tempWater == Int.MAX_VALUE) return true
            return false
        }
        if(getPlaceNowForecast(place) == null) return true
        return false
    }

    /**
     * Sjekker om et sted skal ha rød eller blaa boolge
     * @param place: Stedet man vil sjekke
     */
    fun redWave(place: Place): Boolean{
        if(personalPreference.value?.get(0)?.waterTempMid!! <= place.tempWater) return true
        return false
    }


}