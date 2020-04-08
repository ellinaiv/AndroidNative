package com.example.team11.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.team11.Forecast
import com.example.team11.Place
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class PlaceRepository private constructor() {

    private var places = arrayListOf<Place>()
    private val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"

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
                instance?: PlaceRepository().also { instance = it}
            }
    }

    /**
     * getPlaces funksjonen henter en liste til viewModel med badesteder
     * @return: MutableLiveData<List<Place>>, liste med badesteder
     */
    fun getPlaces(): MutableLiveData<List<Place>>{
        places = fetchPlaces(urlAPI)
        var data = MutableLiveData<List<Place>>()
        data.value = places
        return data
    }


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
        return places
    }

    fun getSeaCurrentSpeed(place: Place) = fetchSeaCurrentSpeed(place)


    private fun fetchSeaCurrentSpeed(place: Place): Double{
        val tag = "tagStromninger"
        val gson = Gson()
        var speed = (-1).toDouble()
        val url = getSpeedUrl(place)
        Log.d(tag, url)
        runBlocking {
            try {
                val response = Fuel.get(url).awaitString()
                val ans = gson.fromJson(response, Forecast::class.java) as Forecast
                ans.ocenaforcasts ?: return@runBlocking
                val oceanForecasts = ans.ocenaforcasts.toMutableList()
                Log.d(tag, oceanForecasts.toString())
                if (oceanForecasts.size > 1){
                    val cast = oceanForecasts[1]
                    Log.d(tag, cast.toString())
                    speed = cast.ocenaforcast.seaSpeed.content.toDouble()
                    Log.d(tag, speed.toString())
                }else{
                    speed = (-1).toDouble()
                }
            }catch (e: Exception){
                Log.e(tag, e.message)
            }

        }
        return speed
    }

    private fun getSpeedUrl(place: Place): String{
        return "http://in2000-apiproxy.ifi.uio.no/weatherapi/oceanforecast/0.9/.json?lat=${place.lat}&lon=${place.lng}"
    }
}


