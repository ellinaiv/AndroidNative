package com.example.team11

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class MapActivity : AppCompatActivity() {
    private val ICON_ID_RED = "ICON_ID_RED"
    private val ICON_ID_YELLOW = "ICON_ID_YELLOW"
    private val ICON_ID_GREEN = "ICON_ID_GREEN "
    private val GEOJSON_ID = "GEOJSON_ID"
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

            if(places.isNotEmpty()){
                mapBoxMap.getStyle{style ->
                    setUpMapImagePins(style)
                    places.forEach { place ->
                        addMarker(place, style)
                    }
                }
            }
        }
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
        val id = place.id.toString() + "_LAYOR_ID"
        val geoId = GEOJSON_ID + place.id.toString()
        val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
            arrayListOf(viewModel.getFeature(place))))
        style.addSource(geoJsonSource)

        val iconId = when(place.preferenceCheck(MIN_TEMP, MID_TEMP)){
            Preference.OPTIMAL -> ICON_ID_GREEN
            Preference.OKEY -> ICON_ID_YELLOW
            Preference.NOT_OKEY -> ICON_ID_RED
        }
        val textOffset: Array<Float> = arrayOf(0f, -2.5f)

        val symbolLayer = SymbolLayer(id, geoId)
        symbolLayer.withProperties(
            PropertyFactory.iconImage(iconId),
            PropertyFactory.textField(place.name),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.textSize(15f),
            PropertyFactory.textOffset(textOffset)
        )
        style.addLayer(symbolLayer)
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
