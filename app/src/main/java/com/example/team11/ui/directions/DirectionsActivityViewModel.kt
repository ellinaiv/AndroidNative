package com.example.team11.ui.directions

import android.content.Context
import androidx.lifecycle.*
import com.example.team11.database.entity.Place
import com.example.team11.Repository.PlaceRepository
import com.example.team11.Transportation
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlin.math.roundToInt
import com.example.team11.R

class DirectionsActivityViewModel(context: Context): ViewModel() {
    var place: LiveData<Place>? = null
    var wayOfTransportation: MutableLiveData<Transportation>? = null
    private var placeRepository: PlaceRepository? = null
    val hasInternet = MutableLiveData<Boolean>()

    /**
     * Setter verdier
     */
    init {
        if(place == null){
            placeRepository = PlaceRepository.getInstance(context)
            place = placeRepository!!.getCurrentPlace()
            wayOfTransportation = placeRepository!!.getWayOfTransportation()
            hasInternet.value = false
        }
    }


    class InstanceCreator(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
            return modelClass.getConstructor(Context::class.java).newInstance(context)
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
     * Endrer måten brukeren ønsker å komme seg til en strand i repository
     * @param way: måten brukeren ønsker å komme seg til stranden
     */
    fun changeWayOfTransportation(way: Transportation){
        placeRepository!!.changeWayOfTransportation(way)
    }

    /**
     * Konverter en gitt tid i sekunder til en string-representasjon
     * i form av timer og minutter. Hvis det tar 0 timer viser man kun antall minutter det
     * tar, og dersom minutter er 0 viser man det kun i timer.
     * @param sec: tid oppgitt i sekunder
     * @return String representasjon av timer og minutter
     */
    fun convertTime(sec: Double?, context: Context): String{
        if(sec == null){
            return context.getString(R.string.no_travel_time)
        }
        val hoursAndMinutes = (sec/3600)
        var hours = hoursAndMinutes.roundToInt()
        if(hoursAndMinutes < hours){
            hours -= 1
        }
        val minutes = ((hoursAndMinutes - hours)*60).roundToInt()
        if(hours == 0){
            return context.getString(R.string.travel_time_minutes, minutes)
        }

        if(minutes == 0){
            return context.getString(R.string.travel_time_hours, hours)
        }

        return context.getString(R.string.travel_time_hours_minutes, hours, minutes)

    }

    /**
     * Gir en string representasjon av lengde i km eller meter. Hvis distansen er
     * mindre enn en km vises det i meter, hvis ikke i km.
     * @param meters: lengden på distansen
     * @return String representasjon av distansen
     */
    fun convertToCorrectDistance(meters: Double?, context: Context): String{
        if(meters == null){
            return context.getString(R.string.no_distance)
        }
        val metersInt = meters.roundToInt()
        val cntDigits = digitsInInt(metersInt)
        if(cntDigits < 4){
            return context.getString(R.string.distance_m, metersInt)
        }

        val km = metersInt.toDouble()/1000
        return context.getString(R.string.distance_km, km)
    }

    /**
     * @param int tallet
     * @return antall tall i tallet 
     */
    private fun digitsInInt(int: Int) = int.toString().toList().size
}