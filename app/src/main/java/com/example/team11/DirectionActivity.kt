package com.example.team11

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.team11.viewmodels.DirectionActivityViewModel
import com.example.team11.viewmodels.PlaceActivityViewModel
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class DirectionActivity : AppCompatActivity() {

    private val viewModel: DirectionActivityViewModel by viewModels{ DirectionActivityViewModel.InstanceCreator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_direction)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        //Observerer stedet som er valgt
        viewModel.place!!.observe(this, Observer { place ->
            //Skriver ut slik at vi kan se om vi har riktig badestrand
            Log.d("tagPlace", place.toString())
            makeMap(place, savedInstanceState)
        })
    }

    private fun makeMap(place: Place, savedInstanceState: Bundle?) {
        val mapView = findViewById<MapView>(R.id.mapView)
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
            mapboxMap.getStyle { style ->
                addDestinationMarker(place, style)
            }
        }
    }

    private fun addDestinationMarker(place: Place, style: Style){
        val ICON_ID_RED = "ICON_ID_RED"
        val geoId = "GEO_ID"
        val icon = BitmapFactory.decodeResource(
            this@DirectionActivity.resources,
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
}
