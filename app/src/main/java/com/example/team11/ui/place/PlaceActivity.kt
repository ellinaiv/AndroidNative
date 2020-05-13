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
import com.example.team11.Place
import com.example.team11.R
import com.example.team11.Transportation
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

class PlaceActivity : AppCompatActivity() {
    private val viewModel: PlaceActivityViewModel by viewModels{ PlaceActivityViewModel.InstanceCreator() }

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

            // henter timesvarsel vær
            viewModel.hourForecast!!.observe(this, Observer { hourForecast ->

            })

            // henter langtidsvarsel vær
            viewModel.dayForecast!!.observe(this, Observer { longForecast ->

            })

            makeAboutPage(place, savedInstanceState)
        })

        Log.d("tagPlace", "ferdig")


    }

    /**
     * Lager infosiden om badeplassen
     * @param place: badeplassen
     * @param savedInstanceState: mapView trenger denne i makeMap
     */
    private fun makeAboutPage(place: Place, savedInstanceState: Bundle?) {
        toggleFavourite.isChecked = place.favorite


        // topBar
        buttonBack.setOnClickListener {
            finish()
        }

        toggleFavourite.setOnCheckedChangeListener { _, isChecked ->
            place.favorite = isChecked
            viewModel.updateFavoritePlaces()
        }


        textPlaceName.text = place.name

        // vann, vær, uv sanntid
        textTempWater.text = getString(R.string.tempC, place.tempWater)
        textTempAir.text = getString(R.string.tempC, place.hourforecast.instant.tempAir)
        textRain.text = getString(R.string.place_rain, place.hourforecast.instant.precipitation)
//        textCurrentsResult.text =         //TODO
//        textUVResult =        //TODO


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
        setForecast(hourForecast.hour1, text1Hour, imageForecast1Hour, textTemp1Hour, textRain1Hour)
        setForecast(hourForecast.hour2, text2Hours, imageForecast2Hours, textTemp2Hours, textRain2Hours)
        setForecast(hourForecast.hour3, text3Hours, imageForecast3Hours, textTemp3Hours, textRain3Hours)
        setForecast(hourForecast.hour4, text4Hours, imageForecast4Hours, textTemp4Hours, textRain4Hours)
        setForecast(hourForecast.hour5, text5Hours, imageForecast5Hours, textTemp5Hours, textRain5Hours)

        // langtidsvarsel vær
        setForecast(dayForecast.day1, textDate1Day, imageForecast1Day, textTemp1Day, textRain1Day)
        setForecast(dayForecast.day2, textDate2Days, imageForecast2Days, textTemp2Days, textRain2Days)
        setForecast(dayForecast.day3, textDate3Days, imageForecast3Days, textTemp3Days, textRain3Days)
        setForecast(dayForecast.day4, textDate4Days, imageForecast4Days, textTemp4Days, textRain4Days)
        setForecast(dayForecast.day5, textDate5Days, imageForecast5Days, textTemp5Days, textRain5Days)


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

    private fun setForecast(forecast: kotlin.Any, time: TextView, symbol: ImageView,
                            temp: TextView, rain: TextView) {
        //TODO(må testes når data er på plass)
        time.text = forecast.time    //TODO(formatere string)
        symbol.setImageDrawable(getDrawable(getResources().getIdentifier(forecast.symbol)))
        temp.text = forecast.tempAir
        rain.text = forecast.precipitation
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
