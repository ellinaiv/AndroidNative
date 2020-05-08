package com.example.team11.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.team11.PersonalPreference
import com.example.team11.Place
import com.example.team11.Transportation
import com.example.team11.api.ApiClient
import com.example.team11.valueObjects.OceanForecast
import com.example.team11.valueObjects.WeatherForecast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.runBlocking
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader

class PlaceRepository private constructor() {

    private var allPlaces = mutableListOf<Place>()
    private var places = MutableLiveData<List<Place>>()
    private val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"
    private var currentPlace = MutableLiveData<Place>()
    private var wayOfTransportation = MutableLiveData<Transportation>()
    private var favoritePlaces = MutableLiveData<List<Place>>()
    private var personalPreferences = MutableLiveData<PersonalPreference>()

    //Kotlin sin static
    companion object {
        @Volatile private var instance: PlaceRepository? = null

        /**
         * getInstance: henter PlaceRepository objekt, hvis det ikke finnes noen
         * eller returneres det et.
         * @return PlaceRepository
         */
        fun getInstance() =
            instance ?: synchronized(this){
                instance?: PlaceRepository().also {
                    instance = it
                    it.personalPreferences.value = PersonalPreference()
                    it.wayOfTransportation.value = Transportation.BIKE
                }
            }
    }

    /**
     * Returnerer en peker til preferansene til brukeren
     * @return brukerens preferance
     */
    fun getPersonalPreferences() = personalPreferences

    /**
     * Oppdaterer preferansene til brukeren
     * @param newPersonalPreference den nye preferansen
     */
    fun updatePersonalPreference(newPersonalPreference: PersonalPreference){
        personalPreferences.value =  newPersonalPreference
        updatePlaces()
    }

    /**
     * Oppdatere listen med steder som liste og kart bruker basert på preferanser
     */
    private fun updatePlaces(){
        val pp = personalPreferences.value!!
        places.value = allPlaces.filter { place ->
            isTempAirOk(pp, place) and isTempWaterOk(pp, place)
        }
    }

    /**
     * Sjekker om et gitt sted har riktig kriterier mtp vanntempratur for å vises
     * @param pp: brukeren sine preferanser
     * @param place stedet som skal sjekkes
     * @return true hvis den oppfyller kriteriene false ellers
     */
    private fun isTempWaterOk(pp: PersonalPreference, place: Place): Boolean{
        return ((pp.showWaterWarm and (place.tempWater >= pp.waterTempMid))
                or (pp.showWaterCold and (place.tempWater < pp.waterTempMid)))
    }

    /**
     * Sjekker om et gitt sted har riktig kriterier mtp luftempratur for å vises
     * @param pp: brukeren sine preferanser
     * @param place stedet som skal sjekkes
     * @return true hvis den oppfyller kriteriene false ellers
     */
    private fun isTempAirOk(pp: PersonalPreference, place: Place): Boolean{
        return ((pp.showAirWarm and (place.tempAir >= pp.airTempMid))
                or (pp.showAirCold and (place.tempAir < pp.airTempMid)))
    }

    /**
     * Returnerer en liste med favoritt stedene til en bruker
     * @return MutableLiveData<List<Place>> liste med brukerens favoritt steder
     */
    fun getFavoritePlaces(): MutableLiveData<List<Place>>{
        if(favoritePlaces.value == null){
            favoritePlaces.value = emptyList()
        }
        return favoritePlaces
    }

    /**
     * Oppdaterer favoritt stedene
     */
    fun updateFavoritePlaces(){
        favoritePlaces.value = allPlaces.filter { place ->  place.favorite}
    }

    /**
     * getPlaces funksjonen henter en liste til viewModel med badesteder
     * @return: MutableLiveData<List<Place>>, liste med badesteder
     */
    fun getPlaces(): MutableLiveData<List<Place>>{
        if (places.value == null){
            allPlaces = fetchPlaces(urlAPI)
            places.value = allPlaces
            updatePlaces()
        }
        return places
    }

    /**
     * endrer currentplace
     * @param place: Stedet som skal endres til å være currentPlace
     */
    fun changeCurrentPlace(place: Place){
        Log.d("tagRepository", "current endra")
        currentPlace.value = place
    }

    /**
     * Henter ut currentPlace
     * @return stedet som er currentPlace
     */
    fun getCurrentPlace() = currentPlace

    /**
     * Endrer måten brukeren ønsker å komme seg til en strand
     * @param way: måten brukeren ønsker å komme seg til stranden
     */
    fun changeWayOfTransportation(way: Transportation){
        wayOfTransportation.value = way
    }

    /**
     * henter ut måten man kommer seg til stranden
     * @return måten brukeren øsnker å komme seg til stranden
     */
    fun getWayOfTransportation() = wayOfTransportation


    /**
     * fetchPlaces funksjonen henter getResponse fra API, parser XML-responsen og oppretter en liste
     * med place-objekter
     * @param: String, urlen til APIet
     * @return: ArrayList<Place>, liste med badesteder
     */

    private fun fetchPlaces(url : String) : ArrayList<Place>{
        val places = arrayListOf<Place>()
        val tag = "getData() ---->"
        runBlocking{

            try {

                val response = Fuel.get(url).awaitString()
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()
                xpp.setInput(StringReader(response))
                var eventType = xpp.eventType

                lateinit var name: String
                lateinit var lat: String
                lateinit var long: String
                var id = 0

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xpp.name == "name") {
                        xpp.next()
                        name = xpp.text
                        xpp.next()
                    } else if (eventType == XmlPullParser.START_TAG && xpp.name == "lat") {
                        xpp.next()
                        lat = xpp.text
                        xpp.next()

                    } else if (eventType == XmlPullParser.START_TAG && xpp.name == "long") {
                        xpp.next()
                        long = xpp.text
                        xpp.next()
                        places.add(Place(id++, name, lat.toDouble(), long.toDouble()))
                    }

                    eventType = xpp.next()

                }
            } catch (e: Exception) {
                Log.e(tag, e.message.toString())
            }
        }
        //Bare for at det skal logges, så man kan se at det funker
        for(i in places){
            fetchWeather(i)
        }
        return places
    }

    /**
     * Henter ut hvor mye strømninger det er på en gitt badestrand
     * @param place stranden man ønsker å vite strømningen på
     * @return en Double. Hvis veriden < 0 er det ikke noen målinger på det stedet
     */
    fun getSeaCurrentSpeed(place: Place) = fetchSeaCurrentSpeed(place)


    /**
     * Henter strømningene til et sted fra met sitt api.
     * @param place stranden man ønsker å vite strømningen på
     * @return en Double. Hvis verdien < 0 er det ikke noen målinger på det stedet
     */
    private fun fetchSeaCurrentSpeed(place: Place): Double {
        val tag = "tagFetchCurrentSeaSpeed"
        var speed = (-1).toDouble()

        val call=
            ApiClient.build()?.getSeaSpeed(place.lat, place.lng)

        call?.enqueue(object : Callback<OceanForecast> {
            override fun onResponse(call: Call<OceanForecast>, response: Response<OceanForecast>) {
                if (response.isSuccessful){
                    val oceanForecasts = response.body()?.OceanForecastLayers
                    if ((oceanForecasts != null) && (oceanForecasts.size > 1)) {

                        // Verdien til speed blir bare endret dersom seaSpeed.content != null
                        response.body()?.OceanForecastLayers?.get(1)?.OceanForecastDetails?.seaSpeed?.content?.toDouble()
                            ?.let { speed = it }
                        Log.d(tag, place.toString())
                        Log.d(tag, speed.toString())
                    }
                }
            }
            override fun onFailure(call: Call<OceanForecast>, t: Throwable) {
                Log.v(tag, "error in fetchCurrentSeaSpeed")
            }
        })
        return speed
    }

    /**
     * Henter forecast til et sted fra met sitt api.
     * @param place stranden man ønsker forecast for
     * @return Når returnerer den bare temperatur, må se ann hvordan det skal være når databasen er på plass
     *
     */

    fun fetchWeather(place: Place): String? {
        val tag = "tagWeather"
        val temp = null;

        val call= ApiClient.build()?.getWeather(place.lat, place.lng)

        call?.enqueue(object : Callback<WeatherForecast> {
            override fun onResponse(call: Call<WeatherForecast>, response: Response<WeatherForecast>) {
                if (response.isSuccessful){
                    Log.d(tag, response.body().toString())
                    val temp = response.body()?.weatherForecastTimeSlotList?.list?.get(0)?.types?.instantWeatherForecast?.details?.temp
                    Log.d(tag, temp.toString())
                }
            }
            override fun onFailure(call: Call<WeatherForecast>, t: Throwable) {
                Log.d(tag, "error")
            }
        })
        return temp
    }


}


