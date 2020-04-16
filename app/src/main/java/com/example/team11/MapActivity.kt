package com.example.team11

import android.graphics.BitmapFactory
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng

class MapActivity : AppCompatActivity(), MapboxMap.OnMapClickListener {
    private val ICON_ID_RED = "ICON_ID_RED"
    private val ICON_ID_BLUE = "ICON_ID_BLUE "
    private val GEOJSON_ID = "GEOJSON_ID"
    private val LAYOR_ID = "LAYOR_ID:"
    private var listOfLayerId = mutableListOf<String>()
    private val propertyId = "PROPERTY_ID"

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
                Log.d("MapTg: ", place.toString())
            }
            makeMap(places)
        })
    }

    /**
     * Tegner opp kartet og passer på at alle steden blir plassert på kartet
     * @param places: en liste med steder som skal plasseres på kartet
     */
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

    /**
     * Det er denne metoden som registrerer om trykket faktisk traff et punkt på kartet, eller
     * ikke, og hva man i såfall skal gjøre når man har trykket på noe. Her vil den da vise
     * et kort med informasjon om badestedet
     * @param screenPoint: det stedet på skjermen hvor brukeren trykket
     * @return Boolean: true, hvis det er et sted vi kan trykke på, false ellers
     */
    private fun handleClickIcon(screenPoint: PointF): Boolean{
        Log.d("tag", "håndterer det")
        val features = filterLayer(screenPoint)
        if(features.isNotEmpty()){
            val feature = features[0]
            val place = viewModel.places!!.value!!.filter {
                it.id == (feature.getNumberProperty(propertyId).toInt()) }[0]
            Log.d("tag", place.toString())
            showPlace(place)
            return true
        }
        removePlace()
        return false
    }

    /**
     * Filtrerer slik at kun de layeren som er et sted blir registert (Det ligger allerede
     * noen layer automatisk inne i kartet. Og legger til de featurene som er i nærheten av
     * der brukeren trykket
     * @param screenPoint: det stedet på skjermen hvor brukeren trykket
     * @return List<Feature> en liste med alle steden som ble registrert i nærheten av trykket
     */
    private fun filterLayer(screenPoint: PointF): List<Feature>{
        val feature = mutableListOf<Feature>()
        for(id in listOfLayerId){
            val oneLayer = mapBoxMap.queryRenderedFeatures(screenPoint, id)
            feature.addAll(oneLayer)
        }
        return feature
    }

    /**
     * Legger til kort over kartviewet, med infomrasjon om en bestemt badestrand
     * @param place: Stedet som skal ha informasjonen sin på display
     */
    private fun showPlace(place: Place){
        val nameTextView = findViewById<TextView>(R.id.namePlace)
        val placeViewHolder = findViewById<ConstraintLayout>(R.id.placeViewHolder)
        val tempAirText = findViewById<TextView>(R.id.tempAirText)
        val tempWaterText = findViewById<TextView>(R.id.tempWaterText)
        val tempWaterImage = findViewById<ImageView>(R.id.tempWaterImage)
        val showPlaceButton = findViewById<ImageButton>(R.id.showPlaceButton)

        showPlaceButton.setOnClickListener{
            Toast.makeText(this, place.toString(), Toast.LENGTH_LONG).show()
        }

        when(place.isWarm()){
            true -> tempWaterImage.setImageResource(R.drawable.drop_red)
            false -> tempWaterImage.setImageResource(R.drawable.drop_blue)
        }

        nameTextView.text = place.name
        tempAirText.text = getString(R.string.noData)
        tempWaterText.text = getString(R.string.tempC, place.temp)
        placeViewHolder.visibility = View.VISIBLE

        //zoomer til stedet på kartet
        val position = CameraPosition.Builder()
            .target(LatLng(place.lat, place.lng))
            .zoom(15.0)
            .build()
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)
    }

    /**
     * Fjerner kortet som hviser informasjonen om et sted
     */
    private fun removePlace(){
        val placeViewHolder = findViewById<ConstraintLayout>(R.id.placeViewHolder)
        placeViewHolder.visibility = View.GONE
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
            R.drawable.blue_marker
        )

        if(icon == null) Log.d(tag, "BLUE")
        style.addImage(ICON_ID_BLUE, icon)
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

        val iconId = when(place.isWarm()){
            true -> ICON_ID_RED
            false -> ICON_ID_BLUE
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

    override fun onStart() {
        super.onStart();
        mapView.onStart();
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
