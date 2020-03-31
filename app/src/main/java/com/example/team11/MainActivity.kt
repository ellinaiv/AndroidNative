package com.example.team11

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var places: ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val urlAPI = "http://oslokommune.msolution.no/friluft/badetemperaturer.jsp"
        places = getPlaces(urlAPI)

        /*
         * manuelt testing for badesteder, skal slettes
         */
        for(place in places){
            Log.d("name: ", place.name)
            Log.d("LatLng: ", place.getLatLng().toString())
            Log.d("tmp: ", place.temp.toString())
        }

        //kun får å enn så lenge komme seg til kartet.
        val mapButton = findViewById<Button>(R.id.kartButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply{
                putExtra("PLACES_LIST", places)
            }
            startActivity(intent)
        }


    }
    /**
     * getPlaces funksjonen henter getResponse fra API, parser XML-responsen og oppretter en liste
     * med place-objekter
     * @param: String, urlen til APIet
     * @return: ArrayList<Place>, liste med badesteder
     */

    private fun getPlaces(url : String) : ArrayList<Place>{
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
                var id = -1

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
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }
        return places
    }



}


