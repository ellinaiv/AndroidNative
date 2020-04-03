package com.example.team11

import android.graphics.BitmapFactory
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.team11.viewmodels.MapActivityViewModel
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import androidx.lifecycle.Observer
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng

class MapActivity : AppCompatActivity(), MapboxMap.OnMapClickListener {
    private val ICON_ID_RED = "ICON_ID_RED"
    private val ICON_ID_YELLOW = "ICON_ID_YELLOW"
    private val ICON_ID_GREEN = "ICON_ID_GREEN "
    private val GEOJSON_ID = "GEOJSON_ID"
    private val LAYOR_ID = "LAYOR_ID:"
    private var listOfLayerId = mutableListOf<String>()
    private val propertyId = "PROPERTY_ID"
    //Midlertidige verdier fram til preferanses er bestemt
    private val MIN_TEMP = 15
    private val MID_TEMP = 23

    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap

    private val viewModel: MapActivityViewModel by viewModels{MapActivityViewModel.InstanceCreator() }
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        listOfLayerId = mutableListOf<String>()

        viewModel.places!!.observe(this, Observer { places ->
            /*
         * manuelt testing for badesteder, skal slettes
         */
            for(place in places){
                Log.d("MapTag: ", place.toString())
            }
            makeMap(places)
        })
    }

    private fun makeMap(places: List<Place>){
        mapView.getMapAsync {mapBoxMap ->
            this.mapBoxMap = mapBoxMap
            mapBoxMap.setStyle(Style.MAPBOX_STREETS)

            mapBoxMap.getStyle { style ->
                if(places.isNotEmpty()){
                    setUpMapImagePins(style)
                    for(place in places){
                        addMarker(place, style)
                    }
                }
                mapBoxMap.addOnMapClickListener(this)
            }
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        return handleClickIcon(mapBoxMap.projection.toScreenLocation(point))
    }

    private fun handleClickIcon(screenPoint: PointF): Boolean{
        Log.d("tag", "håndterer det")
        val features = filterLayer(screenPoint)
        Log.d("Trykk", features.toString())
        if(features.isNotEmpty()){
            val feature = features[0]
            val place = viewModel.places!!.value!!.filter {
                it.id == (feature.getNumberProperty(propertyId).toInt()) }[0]
            Log.d("tag", place.toString())
            return true
        }
        return false
    }

    private fun filterLayer(screenPoint: PointF): List<Feature>{
        val feature = mutableListOf<Feature>()
        for(id in listOfLayerId){
            val oneLayer = mapBoxMap.queryRenderedFeatures(screenPoint, id)
            feature.addAll(oneLayer)
        }
        return feature
    }



    /**
     * Legger til ikoner som man kan plassere på kartet
     * @param style:  stilen som kartet bruker
     */
    private fun setUpMapImagePins(style: Style){
        var icon = BitmapFactory.decodeResource(
            this@MapActivity.resources,
            R.drawable.mapbox_marker_icon_default
        )

        val tag = "Legg til icon"

        if(icon == null) Log.d(tag, "RED")

        style.addImage(ICON_ID_RED, icon)

        icon = BitmapFactory.decodeResource(
            this@MapActivity.resources,
            R.drawable.yellow_marker
        )
        if(icon == null) Log.d(tag, "YELLOW")

        style.addImage(ICON_ID_YELLOW, icon)

        icon = BitmapFactory.decodeResource(
            this@MapActivity.resources,
            R.drawable.blue_marker
        )

        if(icon == null) Log.d(tag, "GREEN")
        style.addImage(ICON_ID_GREEN, icon)
    }


    /**
     * Legger til en marker for en place. Legger til farge på markeren etter Preferance og
     * legger til navnet på stedet
     *
     * @param place: Stedet som skal plasseres på kartet
     * @param style: Stilen på kartet
     */
    private fun addMarker(place: Place, style: Style){
        val id = LAYOR_ID + place.id.toString()
        val geoId = GEOJSON_ID + place.id.toString()
        val feature = viewModel.getFeature(place)
        feature.addNumberProperty(propertyId, place.id)
        val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
            arrayListOf(feature)))
        style.addSource(geoJsonSource)

        val iconId = when(place.preferenceCheck(MIN_TEMP, MID_TEMP)){
            Preference.OPTIMAL -> ICON_ID_GREEN
            Preference.OKEY -> ICON_ID_YELLOW
            Preference.NOT_OKEY -> ICON_ID_RED
        }

        val symbolLayer = SymbolLayer(id, geoId)
        symbolLayer.withProperties(
            PropertyFactory.iconImage(iconId),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        style.addLayer(symbolLayer)
        listOfLayerId.add(id)
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
