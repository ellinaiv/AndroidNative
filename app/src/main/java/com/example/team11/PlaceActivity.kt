package com.example.team11

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.team11.viewmodels.PlaceActivityViewModel
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
        val namePlace = findViewById<TextView>(R.id.namePlace)
        val directionBikeButton = findViewById<ImageButton>(R.id.directionButtonBike)
        val directionCarButton = findViewById<ImageButton>(R.id.directionButtonCar)
        val directionWalkButton = findViewById<ImageButton>(R.id.directionButtonWalk)
        val tempWater = findViewById<TextView>(R.id.tempWater)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val toggelFavorite = findViewById<ToggleButton>(R.id.toggleFavourite)

        toggelFavorite.isChecked = place.favorite

        toggelFavorite.setOnCheckedChangeListener { _, isChecked ->
            place.favorite = isChecked
        }

        backButton.setOnClickListener {
            finish()
        }

        directionBikeButton.setOnClickListener {
            val intent = Intent(this, DirectionActivity::class.java)
            viewModel.changeWayOfTransportation(Transporatation.BIKE)
            startActivity(intent)
        }

        directionCarButton.setOnClickListener {
            val intent = Intent(this, DirectionActivity::class.java)
            viewModel.changeWayOfTransportation(Transporatation.CAR)
            startActivity(intent)
        }

        directionWalkButton.setOnClickListener {
            val intent = Intent(this, DirectionActivity::class.java)
            viewModel.changeWayOfTransportation(Transporatation.WALK)
            startActivity(intent)
        }

        namePlace.text = place.name
        tempWater.text = getString(R.string.tempC, place.temp)

        makeMap(place, savedInstanceState)
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
