package com.example.team11.Repository

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.database.entity.Place
import com.example.team11.Transportation
import com.example.team11.api.ApiClient
import com.example.team11.database.AppDatabase
import com.example.team11.database.entity.WeatherForecastDb
import com.example.team11.util.Constants
import com.example.team11.database.entity.MetadataTable
import com.example.team11.util.Util.formatToDaysTime
import com.example.team11.util.Util.formatToHoursTime
import com.example.team11.util.Util.getNowHourForecastDb
import com.example.team11.util.Util.getWantedDaysForecastApi
import com.example.team11.util.Util.getWantedForecastDb
import com.example.team11.util.Util.getWantedHoursForecastApi
import com.example.team11.util.Util.shouldFetch
import com.example.team11.valueObjects.OceanForecast
import com.example.team11.valueObjects.WeatherForecastApi
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.runBlocking
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random

class PlaceRepository private constructor(context: Context) {
    private var allPlaces = mutableListOf<Place>()
    private val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"
    private var currentPlace = MutableLiveData<Place>()
    private var wayOfTransportation = MutableLiveData<Transportation>()
    private var favoritePlaces = MutableLiveData<List<Place>>()
    private var personalPreferences = MutableLiveData<PersonalPreference>()
    private val database: AppDatabase = AppDatabase.getInstance(context)
    private val placeDao = database.placeDao()
    private val metadataDao = database.metadataDao()
    private val weatherForecastDao = database.weatherForecastDao()
    private val personalPreferenceDao = database.personalPreferenceDao()


    //Kotlin sin static
    companion object {
        @Volatile
        private var instance: PlaceRepository? = null

        /**
         * getInstance: henter PlaceRepository objekt, hvis det ikke finnes noen
         * eller returneres det et.
         * @return PlaceRepository
         */
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PlaceRepository(context).also {
                    instance = it
                    it.wayOfTransportation.value = Transportation.BIKE


                }
            }
    }

    fun changeFalseData(newFalseData: Boolean) {
        if (personalPreferenceDao.getFalseData() != newFalseData) {
            if(newFalseData){
                personalPreferenceDao.changeToFalseData()
                AsyncTask.execute {
                    val allPlaces = placeDao.getAllPlaces()
                    allPlaces.value?.forEach { place ->
                        placeDao.changeToFalseData(12, place.id, Int.MAX_VALUE)
                    }
                }
            }else{
                personalPreferenceDao.changeToRealData()
                cachePlacesDb(fetchPlaces(urlAPI))
            }
        }
    }

    /**
     * Returnerer en peker til preferansene til brukeren
     * @return brukerens preferance
     */
    fun getPersonalPreferences(): LiveData<List<PersonalPreference>>{
        val pp = personalPreferenceDao.getPersonalPreference()
        Log.d("tagPersonal", pp.value.toString())
        return pp
    }

    /**
     * Oppdaterer preferansene til brukeren
     * @param newPersonalPreference den nye preferansen
     */
    fun updatePersonalPreference(personalPreference: PersonalPreference) {
        AsyncTask.execute {
            personalPreferenceDao.addPersonalPreference(personalPreference)
        }
    }

    /**
     * Returnerer en liste med favoritt stedene til en bruker
     * @return LiveData<List<Place>> liste med brukerens favoritt steder
     */
    fun getFavoritePlaces(): LiveData<List<Place>> {
        val places = placeDao.getFavoritePlaceList()
        Log.d("tagFavoritePlace", "Favorittsteder: ${places.value}")
        return places

    }

    /**
     * Legger til favoritt sted
     */
    fun addFavoritePlace(place: Place) {
        AsyncTask.execute { placeDao.addFavorite(place.id) }
        Log.d("tagFavoritePlace", "Lagt til favorittsted i databasen")
    }

    /**
     * Fjern favoritt sted
     */
    fun removeFavoritePlace(place: Place) {
        AsyncTask.execute { placeDao.removeFavorite(place.id) }
        Log.d("tagFavoritePlace", "Fjernet favorittsted i databasen")
    }

    /**
     * Sjekker om sted er favoritt
     */
    fun isPlaceFavorite(place: Place): LiveData<Boolean> {
        val returnValue = placeDao.isPlaceFavorite(place.id)
        Log.d("tagIsPlaceFavorite", "${returnValue.value}")
        return returnValue
    }

    /**
     * getPlaces funksjonen henter en liste til viewModel med badesteder
     * @return: MutableLiveData<List<Place>>, liste med badesteder
     */
    fun getPlaces(): LiveData<List<Place>> {
        val tag = "tagGetPlaces"
        // TODO("Hvor ofte burde places fetches?")
        // TODO("Kan jeg gjøre non-assertive call her? Dersom favoritePlaces.value er null burde den stoppe å sjekke på første?"
        val places: LiveData<List<Place>> = placeDao.getPlaceList(getNowHourForecastDb()[0])
        Log.d(tag, "getPlaces")
        AsyncTask.execute {
            Log.d("tagDatabase", placeDao.getNumbPlaces().toString())
        }
        AsyncTask.execute {
            if (placeDao.getNumbPlaces() == 0 || shouldFetch(
                    metadataDao,
                    Constants.MEATDATA_ENTRY_PLACE_TABLE,
                    10,
                    TimeUnit.DAYS
                )
            ) {
                Log.d(tag, "fetcherPlaces")
                cachePlacesDb(fetchPlaces(urlAPI))
            }
        }
        return places
    }

    fun getNowForecastsList(places: List<Place>): LiveData<List<WeatherForecastDb>>? {
        val tag = "tagGetForecast"
        val placeIds = places.map { it.id }
        val nowForecasts: LiveData<List<WeatherForecastDb>> =
            weatherForecastDao.getTimeForecastsList(placeIds, getNowHourForecastDb())
        Log.d(tag, "getHourForecast")
        for (place in places) {
            AsyncTask.execute {
                if (shouldFetch(
                        metadataDao,
                        Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + place.id,
                        1,
                        TimeUnit.HOURS
                    )
                ) {
                    Log.d(tag, "fetcherForecast")
                    fetchWeatherForecast(place)
                }
            }
        }
        Log.d("Fra databasen", nowForecasts.toString())
        return nowForecasts
    }


    /**
     * getPlaces funksjonen henter en liste til viewModel med vær for de netse timene
     * @return: LiveData<List<HourForecast>>, liste med badesteder
     */
    fun getForecast(place: Place, hour: Boolean): LiveData<List<WeatherForecastDb>> {
        val tag = "tagGetForecast"
        val forecast: LiveData<List<WeatherForecastDb>> =
            weatherForecastDao.getTimeForecast(place.id, getWantedForecastDb(hour))
        Log.d(tag, "getForecast")

        AsyncTask.execute {
            if (shouldFetch(
                    metadataDao,
                    Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + place.id,
                    1,
                    TimeUnit.HOURS
                )
            ) {
                Log.d(tag, "fetcherForecast")
                fetchWeatherForecast(place)
            }
        }
        Log.d("Fra databasen", forecast.toString())
        return forecast
    }


    fun cachePlacesDb(places: List<Place>) {
        Log.d("tagDatabase", "Lagrer nye steder")
        metadataDao.updateDateLastCached(
            MetadataTable(
                Constants.MEATDATA_ENTRY_PLACE_TABLE,
                currentTimeMillis()
            )
        )
        placeDao.insertPlaceList(places)
    }

    fun cacheWeatherForecastDb(weatherForecast: List<WeatherForecastDb>, placeId: Int) {
        Log.d("tagDatabase", weatherForecast.toString())
        metadataDao.updateDateLastCached(
            MetadataTable(
                Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + placeId.toString(),
                currentTimeMillis()
            )
        )
        weatherForecastDao.insertWeatherForecast(weatherForecast)
    }

    /**
     * endrer currentplace
     * @param place: Stedet som skal endres til å være currentPlace
     */
    fun changeCurrentPlace(place: Place) {
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
    fun changeWayOfTransportation(way: Transportation) {
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

    private fun fetchPlaces(url: String): List<Place> {
        val places = ArrayList<Place>()
        val tag = "getData() ---->"
        runBlocking {
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
                var tempWater = Int.MAX_VALUE
                var id = 0

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && xpp.name == "place") {
                        for (i in 0 until xpp.attributeCount) {
                            val attrName = xpp.getAttributeName(i)
                            if (attrName != null && attrName == "id") {
                                id = xpp.getAttributeValue(i).toInt()
                            }
                        }
                    } else if (eventType == XmlPullParser.START_TAG && xpp.name == "name") {
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
                    } else if (eventType == XmlPullParser.START_TAG && xpp.name == "temp_vann") {
                        if (xpp.next() != XmlPullParser.END_TAG) {
                            tempWater = xpp.text.toInt()
                            xpp.next()
                        }
                        Log.d("tag2", tempWater.toString())
                        places.add(
                            Place(
                                id++,
                                name,
                                lat.toDouble(),
                                long.toDouble(),
                                tempWater
                            )
                        )
                    }

                    eventType = xpp.next()

                }
            } catch (e: Exception) {
                Log.e(tag, e.message.toString())
            }
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

        val call =
            ApiClient.build()?.getSeaSpeed(place.lat, place.lng)

        call?.enqueue(object : Callback<OceanForecast> {
            override fun onResponse(call: Call<OceanForecast>, response: Response<OceanForecast>) {
                if (response.isSuccessful) {
                    val oceanForecasts = response.body()?.OceanForecastLayers
                    if ((oceanForecasts != null) && (oceanForecasts.size > 1)) {

                        // Verdien til speed blir bare endret dersom seaSpeed.content != null
                        response.body()?.OceanForecastLayers?.get(1)
                            ?.OceanForecastDetails?.seaSpeed?.content?.toDouble()
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
    fun fetchWeatherForecast(place: Place) {
        val tag = "tagWeather"
        val wantedForecastDb = ArrayList<WeatherForecastDb>()
        val call = ApiClient.build()?.getWeather(place.lat, place.lng)

        call?.enqueue(object : Callback<WeatherForecastApi> {
            override fun onResponse(
                call: Call<WeatherForecastApi>,
                response: Response<WeatherForecastApi>
            ) {
                if (response.isSuccessful) {
                    val wantedTimesHours = getWantedHoursForecastApi()
                    val wantedTimesDays = getWantedDaysForecastApi()
                    val wantedForecastApi =
                        response.body()?.weatherForecastTimeSlotList?.list?.filter {
                            it.time in wantedTimesHours || it.time in wantedTimesDays
                        }
                    wantedForecastApi!!.forEach { forecast ->
                        var nextHours = forecast.types.nextOneHourForecast
                        var time = formatToHoursTime(forecast.time)

                        if (nextHours == null) {
                            nextHours = forecast.types.nextSixHourForecast
                            time = formatToDaysTime(forecast.time)
                        }
                        wantedForecastDb.add(
                            WeatherForecastDb(
                                place.id,
                                time,
                                nextHours!!.summary.symbol,
                                forecast.types.instantWeatherForecast.details.temp.toInt(),
                                nextHours.details.rainAmount,
                                forecast.types.instantWeatherForecast.details.uv
                            )
                        )
                    };
                    AsyncTask.execute { cacheWeatherForecastDb(wantedForecastDb, place.id) }
                }
            }

            override fun onFailure(call: Call<WeatherForecastApi>, t: Throwable) {
                Log.d(tag, "error")
            }
        })
    }
}






