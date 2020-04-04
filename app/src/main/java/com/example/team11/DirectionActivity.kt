package com.example.team11

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.team11.viewmodels.DirectionActivityViewModel
import com.example.team11.viewmodels.PlaceActivityViewModel
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class DirectionActivity : AppCompatActivity() , PermissionsListener {

    private val viewModel: DirectionActivityViewModel by viewModels{ DirectionActivityViewModel.InstanceCreator() }
    private var permissionManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap

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
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
            mapboxMap.getStyle { style ->
                addDestinationMarker(place, style)
                getRoute(place, style)
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

    private fun getRoute(destination: Place, style: Style){
        enableLocationComponent(style)
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(style: Style){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            val customLocationComponentOptions =
                LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyColor(ContextCompat.getColor(this@DirectionActivity, R.color.mapbox_blue))
                    .build()

            val locationComponentActivityOptions =
                LocationComponentActivationOptions.builder(this, style)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap.locationComponent.apply {
                activateLocationComponent(locationComponentActivityOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.NORMAL
            }
        }else{
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "Trengs for å vise deg veien til strand biaaatch", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableLocationComponent(mapboxMap.style!!)
        }else{
            Toast.makeText(this, "Kan ikke vise vei uten lokasjon", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
