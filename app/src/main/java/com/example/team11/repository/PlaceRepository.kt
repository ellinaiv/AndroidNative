package com.example.team11.repository

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
import com.example.team11.util.Util
import com.example.team11.util.Util.formatToDaysTime
import com.example.team11.util.Util.formatToHoursTime
import com.example.team11.util.Util.getNowHourForecastDb
import com.example.team11.util.Util.getWantedDaysForecastApi
import com.example.team11.util.Util.getWantedForecastDb
import com.example.team11.util.Util.getWantedHoursForecastApi
import com.example.team11.util.Util.shouldFetch
import com.example.team11.api.valueObjects.OceanForecast
import com.example.team11.api.valueObjects.WeatherForecastApi
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class PlaceRepository private constructor(context: Context) {
    private val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"
    private var currentPlace = MutableLiveData<Place>()
    private var wayOfTransportation = MutableLiveData<Transportation>()
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

    /**
     * Tar en boolean om man ønsker falsk data eller ikke. Oppdater Personal Preference databasen.
     * Oppdaterer place databasen med enten falsk data, eller henter data på nytt fra api.
     * @param newFalseData boolean for om bruker ønsker falsk data eller ikke.
     */
    fun changeFalseData(newFalseData: Boolean) {
        GlobalScope.launch{
            if (personalPreferenceDao.getFalseData() != newFalseData) {
                personalPreferenceDao.changeFalseData(newFalseData)
                if(newFalseData){
                    placeDao.changeToFalseData(Int.MAX_VALUE, Constants.waterTempHigh, Constants.waterTempLow)
                }else{
                    cachePlacesDb(fetchPlaces(urlAPI))
                }
            }
        }
    }

    /**
     * Returnerer en peker til preferansene til brukeren
     * @return brukerens preferance
     */
    fun getPersonalPreferences() = personalPreferenceDao.getPersonalPreference()

    /**
     * Oppdaterer preferansene til brukeren
     * @param personalPreference den nye preferansen
     */
    fun updatePersonalPreference(personalPreference: PersonalPreference) {
        AsyncTask.execute {
            personalPreference.falseData = personalPreferenceDao.getFalseData()
            personalPreferenceDao.addPersonalPreference(personalPreference)
        }
    }

    /**
     * Returnerer en liste med favoritt stedene til en bruker
     * @return LiveData<List<Place>> liste med brukerens favoritt steder
     */
    fun getFavoritePlaces() = placeDao.getFavoritePlaceList()

    /**
     * Marekerer et sted som favorittsted
     * @param place man ønsker å sette som favorittsted
     */
    fun addFavoritePlace(place: Place) {
        AsyncTask.execute { placeDao.addFavorite(place.id) }
    }

    /**
     * Fjerner markeringen for et sted som favorittsted
     * @param place man ønsker å fjerne som favorittsted
     */
    fun removeFavoritePlace(place: Place) {
        AsyncTask.execute { placeDao.removeFavorite(place.id) }
    }

    /**
     * Sjekker om sted er favoritt
     * @param place sted man ønsker å sjekke
     */
    fun isPlaceFavorite(place: Place) = placeDao.isPlaceFavorite(place.id)

    /**
     * Metode som sjekker om data burde hentes fra API. Først returnerers places.
     * Samtidig kjører en asynkron tråd dersom info om steder burde hentes på nytt.
     * Data om steder hentes og caches dersom den burde hentes på nytt.
     * Legges nye data inn i databasen oppdateres livedata automatisk.
     * @return: MutableLiveData<List<Place>>, liste med badesteder
     */
    fun getPlaces(): LiveData<List<Place>> {
        val places: LiveData<List<Place>> = placeDao.getPlaceList(getNowHourForecastDb(
            currentTimeMillis())[0])
        GlobalScope.launch {
            if (placeDao.getNumbPlaces() == 0 || shouldFetch(
                    metadataDao,
                    Constants.MEATDATA_ENTRY_PLACE_TABLE,
                    1,
                    TimeUnit.DAYS
                )
            ) {
                cachePlacesDb(fetchPlaces(urlAPI))
            }
        }
        return places
    }

    /**
     * Returnerer en liste med alle forecast akkuratt nå. Sjekker også om data om forecast
     * burde hentes på nytt.
     * @param places Alle steder der man vil ha now-forecast for
     * @return Livedata<List<WeatherForecastDd>> for alle steder i places
     */

    fun getNowForecastsList(places: List<Place>): LiveData<List<WeatherForecastDb>>? {
        val placeIds = places.map { it.id }
        val nowForecasts: LiveData<List<WeatherForecastDb>> =
            weatherForecastDao.getTimeForecastsList(placeIds, getNowHourForecastDb(currentTimeMillis()))
        for (place in places) {
            AsyncTask.execute {
                if (weatherForecastDao.getNumbForecast() == 0 || shouldFetch(
                        metadataDao,
                        Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + place.id,
                        1,
                        TimeUnit.HOURS
                    )
                ) {
                    fetchWeatherForecast(place)
                    fetchSeaCurrentSpeed(place)
                }
            }
        }
        return nowForecasts
    }


    /**
     * getPlaces funksjonen henter en liste til viewModel med vær for de netse timene
     * Sjekker om data om forecasts burde oppdateres.
     * @return: LiveData<List<HourForecast>>, liste med badesteder
     */
    fun getForecast(place: Place, hour: Boolean): LiveData<List<WeatherForecastDb>> {
        val forecast: LiveData<List<WeatherForecastDb>> = weatherForecastDao.getTimeForecast(
            place.id,
            getWantedForecastDb(hour, currentTimeMillis())
        )

        AsyncTask.execute {
            if (weatherForecastDao.getNumbForecast() == 0 || shouldFetch(
                    metadataDao,
                    Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + place.id,
                    1,
                    TimeUnit.HOURS
                )
            ) {
                fetchWeatherForecast(place)
            }
        }
        return forecast
    }

    /**
     * @param places Liste med steder som skal legges inn i databasen
     * Lagrer data i databasen, og oppdaterer metadatabasen om når data sist ble lagret.
     */
    private fun cachePlacesDb(places: List<Place>) {
        placeDao.insertPlaceList(places)
        metadataDao.updateDateLastCached(
            MetadataTable(
                Constants.MEATDATA_ENTRY_PLACE_TABLE,
                currentTimeMillis()
            )
        )
    }

    /**
     * @param [weatherForecast] List of weatherforecasts to cache [placeId] Id of place beeing cached
     */
    fun cacheWeatherForecastDb(weatherForecast: List<WeatherForecastDb>, placeId: Int) {
       weatherForecastDao.deleteForecastsForPlace(placeId)
        weatherForecastDao.insertWeatherForecast(weatherForecast)
        metadataDao.updateDateLastCached(
            MetadataTable(
                Constants.METADATA_ENTRY_WEATHER_FORECAST_TABLE + placeId.toString(),
                currentTimeMillis()
            )
        )
    }

    /**
     * endrer currentplace
     * @param place: Stedet som skal endres til å være currentPlace
     */
    fun changeCurrentPlace(place: Place) {
        currentPlace.value = place
    }

    /**
     * Henter ut currentPlaceinneholder de ulike metodene for å hente ulik data
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

    private suspend fun fetchPlaces(url: String): List<Place>{
        var places = listOf<Place>()
        try {
            val response = Fuel.get(url).awaitString()
            places = Util.parseXMLPlace(response)
            places.forEach { place ->
                if (placeDao.placeExists(place.id)) {
                    place.favorite = placeDao.isPlaceFavoriteNonLiveData(place.id)
                }
            }
        } catch (e: Exception) {
        }
        return places
    }


    /**
     * Henter strømningene til et sted fra met sitt api.
     * @param place stranden man ønsker å vite strømningen på
     * @return en Double. Hvis verdien < 0 er det ikke noen målinger på det stedet
     */
    private fun fetchSeaCurrentSpeed(place: Place) {
        val tag = "tagFetchCurrentSeaSpeed"

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
                            ?.let {
                                AsyncTask.execute{weatherForecastDao.addSpeed(place.id, it)}
                            }
                    }
                }
            }

            override fun onFailure(call: Call<OceanForecast>, t: Throwable) {
                Log.v(tag, "error in fetchCurrentSeaSpeed")
            }
        })
    }


    /**
     * Henter forecast til et sted fra met sitt api og legger nødvendig infromasjon inn i nye objekter
     * av klassen [WeatherForecastDb].
     * @param place stedet man ønsker forecast for
     */
    private fun fetchWeatherForecast(place: Place) {
        val tag = "tagWeather"
        val wantedForecastDb = ArrayList<WeatherForecastDb>()
        val call = ApiClient.build()?.getWeather(place.lat, place.lng)
        var forecastId = 0

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
                        } ?: emptyList()
                    wantedForecastApi.forEach { forecast ->
                        var time = formatToDaysTime(forecast.time)
                        if (forecast.time in wantedTimesHours) time = formatToHoursTime(forecast.time)
                        var nextHours = forecast.types.nextOneHourForecast
                        if (nextHours == null) {
                            nextHours = forecast.types.nextSixHourForecast
                        }
                        wantedForecastDb.add(
                            WeatherForecastDb(
                                place.id,
                                forecastId++,
                                time,
                                nextHours!!.summary.symbol,
                                forecast.types.instantWeatherForecast.details.temp.toInt(),
                                nextHours.details.rainAmount,
                                forecast.types.instantWeatherForecast.details.uv,
                                (-1).toDouble()
                            )
                        )
                    }
                    AsyncTask.execute { cacheWeatherForecastDb(wantedForecastDb, place.id) }
                }
                fetchSeaCurrentSpeed(place)
            }

            override fun onFailure(call: Call<WeatherForecastApi>, t: Throwable) {
                Log.d(tag, "error")
            }
        })
    }
}






