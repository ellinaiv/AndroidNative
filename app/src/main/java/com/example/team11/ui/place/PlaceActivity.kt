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


class PlaceActivity : AppCompatActivity() {
    private val viewModel: PlaceActivityViewModel by viewModels{
        PlaceActivityViewModel.InstanceCreator(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_place)
        Log.d("tagPlace", "kommet inn ")
        supportActionBar!!.hide()

        //Observerer stedet som er valgt
        viewModel.place!!.observe(this, Observer { place ->
            Log.d("tagPlace", place.toString())

            makeAboutPage(place, savedInstanceState)

            viewModel.isFavorite.observe(this, Observer { isFavorite ->
                toggleFavorite.isChecked = isFavorite
            })
        })

        // henter langtidsvarsel vær
        viewModel.getDayForecast().observe(this, Observer { dayForecast ->
            Log.d("Fra databasen", dayForecast.toString())

            makeDayForecast(dayForecast)
        })

        // henter timesvarsel vær
        viewModel.getHourForecast().observe(this, Observer { hourForecast ->
            Log.d("Fra databasen", hourForecast.toString())
            makeHourForecast(hourForecast)
        })

        Log.d("tagPlace", "ferdig")
    }

    /**
     * Lager infosiden om badeplassen
     *
     * @param place badeplassen
     * @param savedInstanceState mapView trenger denne i makeMap
     */
    private fun makeAboutPage(place: Place, savedInstanceState: Bundle?) {
        buttonBack.setOnClickListener {
            finish()
        }

        textPlaceName.text = place.name

        // vanntemperatur
        //TODO(hente fra database og ikke place)
        if (place.tempWater != Int.MAX_VALUE) {
            textTempWater.text = getString(R.string.tempC, place.tempWater)
            when(viewModel.redWave(place)){
               // true -> imageWater.setImageDrawable(getDrawable(R.drawable.water_red))
                // false -> imageWater.setImageDrawable(getDrawable(R.drawable.water_blue))
            }
        }


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
     *lager langtidsvarsel for vær (kaller på setForecastViews)
     *
     * @param forecast liste med objekter som inneholder værdata
     */
    private fun makeDayForecast(forecast: List<WeatherForecastDb>) {
        if (forecast.isEmpty()) {
            return
        } else {
            setForecastViews(forecast[0], textDate1Day, imageForecast1Day,
                textTemp1Day, textRain1Day)
            setForecastViews(forecast[1], textDate2Days, imageForecast2Days,
                textTemp2Days, textRain2Days)
            setForecastViews(forecast[2], textDate3Days, imageForecast3Days,
                textTemp3Days, textRain3Days)
            setForecastViews(forecast[3], textDate4Days, imageForecast4Days,
                textTemp4Days, textRain4Days)
            setForecastViews(forecast[4], textDate5Days, imageForecast5Days,
                textTemp5Days, textRain5Days)
        }
    }


    /**
     * setter våværende vær,
     * setter verdier i views for havstrømninger og uv,
     * lager timesvarsel for vær (kaller på setForecastViews)
     *
     * @param forecast liste med objekter som inneholder værdata
     */
    private fun makeHourForecast(forecast: List<WeatherForecastDb>) {
        if (forecast.isEmpty()) {
            return
        } else {
            // vær nå
            if (forecast[0].tempAir.toInt() != Int.MAX_VALUE){
                textTempAir.text = getString(R.string.tempC, forecast[0].tempAir.toInt())
                imageWeather.setImageDrawable(getDrawable(resources.getIdentifier(forecast[0].symbol,
                    "drawable", this.packageName)))
                textRain.text = getString(R.string.place_rain, forecast[0].precipitation)
            }

            // havstrømninger
            //TODO
//        var currentsText = convertCurrents(hourForecast[0].currents)
//        textCurrentsResult.text = currentsText
//        textUVResult.setTextColor(getColor(
//            resources.getIdentifier(getTextColor(currentsText, "currents"),
//                "color", this.packageName)))

            // uv
            val uvText = convertUV(forecast[0].uv.toInt())
            textUVResult.text = uvText
            textUVResult.setTextColor(getColor(
                resources.getIdentifier(getTextColor(uvText, "uv"),
                    "color", this.packageName)))
            Log.d("Her er det!", forecast.toString())

            setForecastViews(forecast[1], text1Hour, imageForecast1Hour,
                textTemp1Hour, textRain1Hour)
            setForecastViews(forecast[2], text2Hours, imageForecast2Hours,
                textTemp2Hours, textRain2Hours)
            setForecastViews(forecast[3], text3Hours, imageForecast3Hours,
                textTemp3Hours, textRain3Hours)
            setForecastViews(forecast[4], text4Hours, imageForecast4Hours,
                textTemp4Hours, textRain4Hours)
            setForecastViews(forecast[5], text5Hours, imageForecast5Hours,
                textTemp5Hours, textRain5Hours)
        }
        Log.d("tagPlace", "makeHourForecast ferdig")
    }


    /**
     * setter data for klokkeslett/dato, værikon, temperatur
     * og nedbørsmengde i korresponderende views
     *
     * @param forecast objekt som inneholder data for værmelding
     * @param time textView som viser gjeldende tid (klokkeslett/dato) for værvarsel
     * @param symbol imageView som viser værikonet for gjeldende tid
     * @param temp textView som viser lufttemperatur for gjeldende tid
     * @param rain textView som viser nedbørsmengde for gjeldende tid
     * @param isHourForecast true hvis værvarselet er timesvarsel, false hvis langtidsvarsel
     */
    private fun setForecastViews(forecast: WeatherForecastDb, time: TextView, symbol: ImageView,
                                 temp: TextView, rain: TextView) {
        if (forecast.tempAir.toInt() == Int.MAX_VALUE) {
            // ingen værdata
            return
        } else {
            // tilgjengelig værdata
            time.text = forecast.time
            temp.text = forecast.tempAir.toInt().toString()
            rain.text = forecast.precipitation.toString()
            symbol.setImageDrawable(getDrawable(resources.getIdentifier(forecast.symbol,
                "drawable", this.packageName)))
        }
    }


    /**
     * konverterer en gitt verdi av UV-stråling til en av de definerte kategoriene
     *
     * @param value verdi for måling av UV-stråling
     * @return en stringrepresentasjon av den tilsvarende kategorien
     */
    private fun convertUV(value: Int): String = when {
        value in 1..2 -> getString(resources.getIdentifier("place_info_weak",
            "string", this.packageName))
        value in 3..5 -> getString(resources.getIdentifier("place_info_moderate",
            "string", this.packageName))
        value in 6..7 -> getString(resources.getIdentifier("place_info_strong",
            "string", this.packageName))
        value in 8..10 -> getString(resources.getIdentifier("place_info_very_strong",
            "string", this.packageName))
        value > 10 -> getString(resources.getIdentifier("place_info_extreme",
            "string", this.packageName))
        else -> getString(resources.getIdentifier("no_data",
            "string", this.packageName))
    }


    /**
     * konverterer en gitt verdi av havstrømninger til en av de definerte kategoriene
     *
     * @param value verdi for måling av havstrømninger
     * @return en stringrepresentasjon av den tilsvarende kategorien
     */
    private fun convertCurrents(value: Int): String {
        ///TODO
        return getString(resources.getIdentifier("no_data",
            "string", this.packageName))
    }


    /**
     * finner riktig tekstfarge for views som viser havstrømninger og UV-stråling
     *
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
     * Tegner kartet til stedet man er på nå, og zoomer inn på det.
     *
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
