package com.example.team11.ui.place

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.team11.database.entity.Place
import com.example.team11.R
import com.example.team11.Transportation
import com.example.team11.database.entity.WeatherForecastDb
import com.example.team11.ui.directions.DirectionsActivity
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_place.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class PlaceActivity : AppCompatActivity() {
    private val viewModel: PlaceActivityViewModel by viewModels{ PlaceActivityViewModel.InstanceCreator(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_place)
        Log.d("tagPlace", "kommet inn ")
        supportActionBar!!.hide()

        //Observerer stedet som er valgt
        viewModel.place!!.observe(this, Observer { place ->
            //Skriver ut slik at vi kan se om vi har riktig badestrand
            Log.d("tagPlace", place.toString())

            // henter langtidsvarsel vær
            viewModel.getDayForecast().observe(this, Observer { dayForecast ->
                // henter timesvarsel vær
                viewModel.getHourForecast().observe(this, Observer { hourForecast ->
                    makeAboutPage(place, savedInstanceState, dayForecast, hourForecast)
                })
            })

            viewModel.isFavorite.observe(this, Observer { isFavorite ->
                toggleFavorite.isChecked = isFavorite
            })
        })
        Log.d("tagPlace", "ferdig")
    }

    /**
     * Lager infosiden om badeplassen
     * @param place badeplassen
     * @param savedInstanceState mapView trenger denne i makeMap
     * @param dayForecast liste med værdata for langtidsvarsel
     * @param hourForecast liste med værdata for timesvarsel
     */
    private fun makeAboutPage(place: Place, savedInstanceState: Bundle?, dayForecast: List<WeatherForecastDb.WeatherForecast>,
                              hourForecast: List<WeatherForecastDb.WeatherForecast>) {
        buttonBack.setOnClickListener {
            finish()
        }

        textPlaceName.text = place.name


        // vær nå, vanntemperatur
        if (place.tempWater != Int.MAX_VALUE) {
            //TODO(hente fra database og ikke place)
            textTempWater.text = getString(R.string.tempC, place.tempWater)
        }

        if (hourForecast[0].tempAir.toInt() != Int.MAX_VALUE){
            textTempAir.text = getString(R.string.tempC, hourForecast[0].tempAir.toInt())
            imageWeather.setImageDrawable(getDrawable(resources.getIdentifier(hourForecast[0].symbol,
                "drawable", this.packageName)))
            textRain.text = getString(R.string.place_rain, hourForecast[0].precipitation)
        }


        // havstrømninger og uv views
        //TODO
//        var currentsText = convertCurrents(hourForecast[0].currents)
//        textCurrentsResult.text = currentsText
//        textUVResult.setTextColor(getColor(
//            resources.getIdentifier(getTextColor(currentsText, "currents"),
//                "color", this.packageName)))

        var uvText = convertUV(hourForecast[0].uv.toInt())
        textUVResult.text = uvText
        textUVResult.setTextColor(getColor(
            resources.getIdentifier(getTextColor(uvText, "uv"),
                "color", this.packageName)))


        // infovinduer om havstrømninger og uv
        buttonCurrentsInfo.setOnClickListener {
            if (layoutUVInfo.visibility == VISIBLE) {
                layoutUVInfo.visibility = GONE
            }
            layoutCurrentsInfo.visibility = VISIBLE
        }

        buttonCurrentsCloseInfo.setOnClickListener {
            layoutCurrentsInfo.visibility = GONE
        }

        linkCurrentsInfoMore.setOnClickListener {
//            TODO(sett inn uri)
//            val link = Intent(Intent.ACTION_VIEW)
//            link.data = Uri.parse("<uri>")
//            startActivity(link)
        }

        buttonUVInfo.setOnClickListener {
            if (layoutCurrentsInfo.visibility == VISIBLE) {
                layoutCurrentsInfo.visibility = GONE
            }
            layoutUVInfo.visibility = VISIBLE
        }

        buttonUVCloseInfo.setOnClickListener {
            layoutUVInfo.visibility = GONE
        }

        linkUVInfoMore.setOnClickListener {
            val link = Intent(Intent.ACTION_VIEW)
            link.data = Uri.parse("https://www.yr.no/uv-varsel")
            startActivity(link)
        }


        // timesvarsel vær
        setForecastViews(hourForecast[1], text1Hour, imageForecast1Hour, textTemp1Hour, textRain1Hour)
        setForecastViews(hourForecast[2], text2Hours, imageForecast2Hours, textTemp2Hours, textRain2Hours)
        setForecastViews(hourForecast[3], text3Hours, imageForecast3Hours, textTemp3Hours, textRain3Hours)
        setForecastViews(hourForecast[4], text4Hours, imageForecast4Hours, textTemp4Hours, textRain4Hours)
        setForecastViews(hourForecast[5], text5Hours, imageForecast5Hours, textTemp5Hours, textRain5Hours)

        // langtidsvarsel vær
        setForecastViews(dayForecast[0], textDate1Day, imageForecast1Day, textTemp1Day, textRain1Day)
        setForecastViews(dayForecast[1], textDate2Days, imageForecast2Days, textTemp2Days, textRain2Days)
        setForecastViews(dayForecast[2], textDate3Days, imageForecast3Days, textTemp3Days, textRain3Days)
        setForecastViews(dayForecast[3], textDate4Days, imageForecast4Days, textTemp4Days, textRain4Days)
        setForecastViews(dayForecast[4], textDate5Days, imageForecast5Days, textTemp5Days, textRain5Days)


        // reisevei og kart
        buttonBike.setOnClickListener {
            val intent = Intent(this, DirectionsActivity::class.java)
            viewModel.changeWayOfTransportation(Transportation.BIKE)
            startActivity(intent)
        }

        buttonCar.setOnClickListener {
            val intent = Intent(this, DirectionsActivity::class.java)
            viewModel.changeWayOfTransportation(Transportation.CAR)
            startActivity(intent)
        }

        buttonWalk.setOnClickListener {
            val intent = Intent(this, DirectionsActivity::class.java)
            viewModel.changeWayOfTransportation(Transportation.WALK)
            startActivity(intent)
        }

        makeMap(place, savedInstanceState)

        linkPublicTransport.setOnClickListener {
            val link = Intent(Intent.ACTION_VIEW)
            link.data = Uri.parse("https://www.ruter.no/")
            startActivity(link)
        }
    }




    /**
     * Tegner kartet til stedet man er på nå, og zommer inn på det.
     * @param place: Stedet som skal vises på kartet
     * @param savedInstanceState: mapView trenger denne til onCreate metoden sin
     */
    private fun makeMap(place: Place, savedInstanceState: Bundle?) {
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
            mapboxMap.getStyle { style ->
                val markerPlace = "ICON"
                val geoId = "GEO_ID"
                val icon = BitmapFactory.decodeResource(
                    this@PlaceActivity.resources,
                    R.drawable.marker_place
                )
                style.addImage(markerPlace, icon)

                val feature = viewModel.getFeature(place)
                val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
                    arrayListOf(feature)))
                style.addSource(geoJsonSource)

                val symbolLayer = SymbolLayer("SYMBOL_LAYER_ID", geoId)
                symbolLayer.withProperties(
                    PropertyFactory.iconImage(markerPlace),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconIgnorePlacement(true)
                )
                style.addLayer(symbolLayer)
            }
            val position = CameraPosition.Builder()
                .target(LatLng(place.lat, place.lng))
                .zoom(15.0)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)
        }
    }


    /**
     * konverterer en gitt verdi av UV-stråling til en av de definerte kategoriene
     * @param value verdi for måling av UV-stråling
     * @return en stringrepresentasjon av den tilsvarende kategorien
     */
    private fun convertUV(value: Int): String = when {
        value in 1..2 -> "Svak"
        value in 3..5 -> "Moderat"
        value in 6..7 -> "Sterk"
        value in 8..10 -> "Svært sterk"
        value > 10 -> "Ekstrem"
        else -> "Ingen data"
    }


    /**
     * konverterer en gitt verdi av havstrømninger til en av de definerte kategoriene
     * @param value verdi for måling av havstrømninger
     * @return en stringrepresentasjon av den tilsvarende kategorien
     */
    private fun convertCurrents(value: Int): String {
        ///TODO
        return "Ingen data"
    }


    /**
     * finner riktig tekstfarge for views som viser havstrømninger og UV-stråling
     * @param text teksten som skal vises i viewet
     * @param type "uv" eller "currents"
     * @return ID for fargekoden i colors.xml
     */
    private fun getTextColor(text: String, type: String): String {
        if (text.equals("Svak"))  {
            return "place_info_low"
        }

        return if (type.equals("uv")) {
            when(text) {
                "Moderat" ->  "place_uv_info_moderate"
                "Sterk" -> "place_uv_info_strong"
                "Svært sterk" -> "place_uv_info_very_strong"
                "Ekstrem" -> "place_uv_info_extreme"
                else -> "mainTextColor"
            }
        } else {
            when(text) {
                "Moderat" -> "place_currents_info_moderate"
                "Sterk" -> "place_currents_info_strong"
                else -> "mainTextColor"
            }
        }
    }


    /**
     * legger inn data for klokkeslett/dato, værikon, temperatur
     * og nedbørsmengde i korresponderende views
     * @param forecast objekt som inneholder data for værmelding
     * @param time textView som viser gjeldende tid (klokkeslett/dato) for værvarsel
     * @param symbol imageView som viser værikonet for gjeldende tid
     * @param temp textView som viser lufttemperatur for gjeldende tid
     * @param rain textView som viser nedbørsmengde for gjeldende tid
     */
    private fun setForecastViews(forecast: WeatherForecastDb.WeatherForecast, time: TextView, symbol: ImageView,
                                 temp: TextView, rain: TextView) {
        if (forecast.tempAir.toInt() == Int.MAX_VALUE) {
            // ingen værdata
            return
        } else {
            // tilgjengelig værdata
            val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            val dateTime = parser.parse(forecast.time)

            if (forecast is WeatherForecastDb.HourForecast) {
                val formatter = SimpleDateFormat("HH")
                time.text = "kl. " + formatter.format(dateTime)
            } else {
                val formatter = SimpleDateFormat("dd/MM")
                time.text = formatter.format(dateTime)
            }

            temp.text = forecast.tempAir.toInt().toString()
            rain.text = forecast.precipitation.toString()
            symbol.setImageDrawable(getDrawable(resources.getIdentifier(forecast.symbol,
                "drawable", this.packageName)))
        }
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        if (toggleFavorite.isChecked) viewModel.addFavoritePlace()
        else viewModel.removeFavoritePlace()
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}
