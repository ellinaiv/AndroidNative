package com.example.team11.uiAndViewModels.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Color
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.database.entity.Place
import com.example.team11.repository.PlaceRepository
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

    fun getNowForecast(placesList: List<Place>): LiveData<List<WeatherForecastDb>>?{
        return placeRepository.getNowForecastsList(placesList)
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
     * Sjekker om stedet er varmt (rodt, blaat (graat hvis det ikke er noe data)
     * @param place: stedet som man vil finne ut om er varmt
     * @return true hvis stedet er varmt, false hvis kaldt
     */
    fun getPinColor(place: Place): Color{
        val personalPreferenceValue = personalPreference.value?.get(0)?: return Color.GRAY
        if(personalPreferenceValue.showBasedOnWater){
            if(place.tempWater == Int.MAX_VALUE) return Color.GRAY
            if(personalPreferenceValue.waterTempMid <= place.tempWater) return Color.RED
            return Color.BLUE
        }
        val tempAir = getPlaceNowForecast(place) ?: return Color.GRAY
        if(personalPreferenceValue.airTempMid <= tempAir.tempAir) return Color.RED
        return Color.BLUE
    }

    /**
     * Sjekker om et sted skal ha graa, rood eller blaa boolge
     * @param place: Stedet man vil sjekke
     * @return fargen boolgen skal veare
     */
    fun colorWave(place: Place): Color{
        if(place.tempWater == Int.MAX_VALUE) return Color.GRAY
        val waterTemp = personalPreference.value?.get(0)?.waterTempMid ?: return Color.GRAY
        if(waterTemp <= place.tempWater) return Color.RED
        return Color.BLUE
    }


}