package com.example.team11

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_map)

        val places = intent.getSerializableExtra("PLACES_LIST") as ArrayList<Place>

        /*
         * manuelt testing for badesteder, skal slettes
         */
        for(place in places){
            Log.d("name: ", place.name)
            Log.d("LatLng: ", place.getLatLng().toString())
            Log.d("tmp: ", place.temp.toString())
        }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

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
     * @param Style: stilen som kartet bruker
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


    private fun addMarker(place: Place, style: Style){
        val id = place.id.toString() + "_LAYOR_ID"
        val geo_id = GEOJSON_ID + place.id.toString()
        val geoJsonSource = GeoJsonSource(geo_id, FeatureCollection.fromFeatures(
            arrayListOf(getFeature(place))))
        style.addSource(geoJsonSource)


        val iconId = when(place.preferenceCheck(MIN_TEMP, MID_TEMP)){
            Preference.OPTIMAL -> ICON_ID_GREEN
            Preference.OKEY -> ICON_ID_YELLOW
            Preference.NOT_OKEY -> ICON_ID_RED
        }
        val textOffset: Array<Float> = arrayOf(0f, -2.5f)

        val symbolLayer = SymbolLayer(id, geo_id)
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


    /**
     * Gir featuren til en Place sin lokasjon (en feature er det som trengs for å vise noe
     * på kartet)
     * @param place: en strand
     * @return en feature verdi basert på lokasjonen til place
     */
    private fun getFeature(place: Place) = Feature.fromGeometry(Point.fromLngLat(place.lng, place.lat))


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
