package com.example.team11.ui.direction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.team11.Place
import com.example.team11.Repository.PlaceRepository
import com.example.team11.Transporatation
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlin.math.roundToInt

class DirectionActivityViewModel: ViewModel() {
    var place: MutableLiveData<Place>? = null
    var wayOfTransporatation: MutableLiveData<Transporatation>? = null
    private var placeRepository: PlaceRepository? = null


    /**
     * Setter verdier
     */
    init {
        if(place == null){
            placeRepository = PlaceRepository.getInstance()
            place = placeRepository!!.getCurrentPlace()
            wayOfTransporatation = placeRepository!!.getWayOfTransportation()
        }
    }


    class InstanceCreator : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor().newInstance()
        }
    }

    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en Feature verdi basert på lokasjonen til place
     */
    fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))!!

    /**
     * Konverter en tid gitt i sekunder til og bli til en string representasjon
     * av timer og minutter. Hvis det tar 0 timer viser man kun mange minutter det
     * tar, og minutter er 0 viser man det kun i timer.
     * @param sec: tid oppgitt i sekunder
     * @return String representasjon av timer og minutter
     */
    fun convertFromSecondsToHoursAndMinutes(sec: Double): String{
        val hoursAndMinutes = (sec/3600)
        var hours = hoursAndMinutes.roundToInt()
        if(hoursAndMinutes < hours){
            hours -= 1
        }
        val minutes = ((hoursAndMinutes - hours)*60).roundToInt()
        if(hours == 0){
            return "$minutes minutter"
        }

        if(minutes == 0){
            return "$hours timer"
        }

        return "$hours timer og $minutes minutter"

    }

    /**
     * Gir en string representasjon av lengde i km eller meter. Hvis distansen er
     * mindre enn en km vises det i meter, hvis ikke i km.
     * @param meters: lengden på distansen
     * @return String representasjon av distansen
     */
    fun convertToRightDistance(meters: Double): String{
        val metersInt = meters.roundToInt()
        val cntDigits = digitsInInt(metersInt)
        if(cntDigits < 4){
            return "$metersInt m"
        }

        val km = metersInt.toDouble()/1000
        return "%.1f km".format(km)
    }

    private fun digitsInInt(int: Int) = int.toString().toList().size
}