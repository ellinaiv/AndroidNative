package com.example.team11.ui.place

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
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
            makeAboutPage(place, savedInstanceState)
        })

        Log.d("tagPlace", "ferdig")


    }

    /**
     * Lager om siden
     * @param place: Stedet som man skal ha informasjon om
     * @param savedInstanceState: mapView trenger denne i makeMap
     */
    private fun makeAboutPage(place: Place, savedInstanceState: Bundle?) {
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        val toggleFavorite = findViewById<ToggleButton>(R.id.toggleFavourite)
        val placeName = findViewById<TextView>(R.id.textPlaceName)
        val tempWater = findViewById<TextView>(R.id.textTempWater)
        val buttonCurrentsInfo = findViewById<ImageButton>(R.id.buttonCurrentsInfo)
        val layoutCurrentsInfo = findViewById<ConstraintLayout>(R.id.layoutCurrentsInfo)
        val currentsCloseInfo = findViewById<ImageButton>(R.id.buttonCurrentsCloseInfo)
        val currentsInfoMore = findViewById<TextView>(R.id.linkCurrentsInfoMore)
        val buttonUvInfo = findViewById<ImageButton>(R.id.buttonUVInfo)
        val layoutUvInfo = findViewById<ConstraintLayout>(R.id.layoutUVInfo)
        val uvCloseInfo = findViewById<ImageButton>(R.id.buttonUVCloseInfo)
        val uvInfoMore = findViewById<TextView>(R.id.linkUVInfoMore)
        val buttonBike = findViewById<ImageButton>(R.id.buttonBike)
        val buttonCar = findViewById<ImageButton>(R.id.buttonCar)
        val buttonWalk = findViewById<ImageButton>(R.id.buttonWalk)
        val linkPublicTransport = findViewById<TextView>(R.id.linkPublicTransport)

        toggleFavorite.isChecked = place.favorite

        buttonBack.setOnClickListener {
            finish()
        }

        toggleFavorite.setOnCheckedChangeListener { _, isChecked ->
            place.favorite = isChecked
            viewModel.updateFavoritePlaces()
        }



        buttonCurrentsInfo.setOnClickListener {
            if (layoutUvInfo.visibility == VISIBLE) {
                layoutUvInfo.visibility = GONE
            }
            layoutCurrentsInfo.visibility = VISIBLE
        }

        currentsCloseInfo.setOnClickListener {
            layoutCurrentsInfo.visibility = GONE
        }

        currentsInfoMore.setOnClickListener {
//            TODO(sett inn uri)
//            val link = Intent(Intent.ACTION_VIEW)
//            link.data = Uri.parse("<uri>")
//            startActivity(link)
        }

        buttonUvInfo.setOnClickListener {
            if (layoutCurrentsInfo.visibility == VISIBLE) {
                layoutCurrentsInfo.visibility = GONE
            }
            layoutUvInfo.visibility = VISIBLE
        }

        uvCloseInfo.setOnClickListener {
            layoutUvInfo.visibility = GONE
        }

        uvInfoMore.setOnClickListener {
            val link = Intent(Intent.ACTION_VIEW)
            link.data = Uri.parse("https://www.yr.no/uv-varsel")
            startActivity(link)
        }



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

        placeName.text = place.name
        tempWater.text = getString(R.string.tempC, place.temp)

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
                val ICON_ID_RED = "ICON_ID_RED"
                val geoId = "GEO_ID"
                val icon = BitmapFactory.decodeResource(
                    this@PlaceActivity.resources,
                    R.drawable.mapbox_marker_icon_default
                )
                style.addImage(ICON_ID_RED, icon)

                val feature = viewModel.getFeature(place)
                val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
                    arrayListOf(feature)))
                style.addSource(geoJsonSource)

                val symbolLayer = SymbolLayer("SYMBOL_LAYER_ID", geoId)
                symbolLayer.withProperties(
                    PropertyFactory.iconImage(ICON_ID_RED),
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
}
