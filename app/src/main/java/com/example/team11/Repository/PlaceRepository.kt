package com.example.team11.Repository

import android.content.Context
import android.os.AsyncTask
import android.os.SystemClock
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.team11.database.entity.Place
import com.example.team11.valueObjects.OceanForecast
import com.example.team11.Transportation
import com.example.team11.api.ApiClient
import com.example.team11.database.AppDatabase
import com.example.team11.database.entity.MetadataTable
import com.example.team11.util.Converters
import com.example.team11.util.DbConstants
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
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.collections.ArrayList

class PlaceRepository private constructor(context: Context) {


    private val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"
    private var currentPlace = MutableLiveData<Place>()
    private var wayOfTransportation = MutableLiveData<Transportation>()
    private var favoritePlaces = MutableLiveData<List<Place>>()
    private val database: AppDatabase = AppDatabase.getInstance(context)
    private val placeDao = database.placeDao()
    private val metadataDao = database.metadataDao()

    //Kotlin sin static
    companion object {
        @Volatile private var instance: PlaceRepository? = null

        /**
         * getInstance: henter PlaceRepository objekt, hvis det ikke finnes noen
         * eller returneres det et.
         * @return PlaceRepository
         */
        fun getInstance(context: Context) =
            instance ?: synchronized(this){
                instance?: PlaceRepository(context).also { instance = it}
            }
    }


    /**
     * Returnerer en liste med favoritt stedene til en bruker
     * @return LiveData<List<Place>> liste med brukerens favoritt steder
     */
    fun getFavoritePlaces(): LiveData<List<Place>>{
        val places =  placeDao.getFavoritePlaceList()
        Log.d("tagFavoritePlace", "Favorittsteder: ${places.value}")
        return places

    }

    /**
     * Legger til favoritt sted
     */
    fun addFavoritePlace(place: Place){
        AsyncTask.execute { placeDao.addFavorite(place.id)}
        Log.d("tagFavoritePlace", "Lagt til favorittsted i databasen")
    }

    /**
     * Fjern favoritt sted
     */
    fun removeFavoritePlace(place: Place){
        AsyncTask.execute { placeDao.removeFavorite(place.id)}
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
        val places: LiveData<List<Place>> = placeDao.getPlaceList()
        Log.d(tag, "getPlaces")

        AsyncTask.execute {
            if (shouldFetch(
                    DbConstants.PLACE_TABLE_NAME,
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

    fun cachePlacesDb(places: List<Place>){
        Log.d("tagDatabase", "Lagrer nye steder")
        metadataDao.updateDateLastCached(MetadataTable(DbConstants.PLACE_TABLE_NAME, currentTimeMillis()))
        placeDao.insertPlaceList(places)
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

    private fun fetchPlaces(url : String) : List<Place>{
        val places = ArrayList<Place>()
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
                var favorite = false
                placeDao.isPlaceFavorite(id)?.value?.let { favorite = it }

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
                        places.add(
                            Place(
                                id++,
                                name,
                                lat.toDouble(),
                                long.toDouble()
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
     * En metode som lager url som skal, man skal hente json elemente på, når
     * det kommer til havstrømninger.
     * @param place: stedet som skal hente ut verdien.
     * @return nettsiden man kan hente ut json elementene fra
     */
    private fun getSpeedUrl(place: Place): String{
        return "http://in2000-apiproxy.ifi.uio.no/weatherapi/oceanforecast/0.9/.json?lat=${place.lat}&lon=${place.lng}"
    }

    // Kode inspirert av:

    private fun shouldFetch(nameDatabase: String, timeout: Int, timeUnit: TimeUnit): Boolean{
        val dateLastFetched = metadataDao.getDateLastCached(nameDatabase)
        val now = currentTimeMillis()
        val timeoutMilli = timeUnit.toMillis(timeout.toLong())
        Log.d("tagDatabase", "dateLastFetched: $dateLastFetched")

        if(dateLastFetched == null){
            return true
        }
        if (now - dateLastFetched > timeoutMilli) {
            Log.d("tagDatabse", "Now: $now, dateLastFetcged: $dateLastFetched, timeoutMilli: $timeoutMilli")
            return true
        }
    return false

    }
}


