package com.example.team11.ui.map

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.team11.Place
import com.example.team11.R
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapFragment : Fragment() {

    private val ICON_ID_RED = "ICON_ID_RED"
    private val ICON_ID_BLUE = "ICON_ID_BLUE "
    private val GEOJSON_ID = "GEOJSON_ID"
    private val LAYOR_ID = "LAYOR_ID:"
    private var listOfLayerId = mutableListOf<String>()
    private val propertyId = "PROPERTY_ID"

    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap

    private lateinit var mapFragmentViewModel: MapFragmentViewModel

    private lateinit var filterPlaces: List<Place>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mapFragmentViewModel =
            ViewModelProviders.of(this).get(MapFragmentViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapFragmentViewModel.places!!.observe(viewLifecycleOwner, Observer {places->
            makeMap(places)
        })
        return root
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
                //mapBoxMap.addOnMapClickListener(this)
            }
        }
    }
    /**
     * Legger til ikoner som man kan plassere på kartet
     * @param style:  stilen som kartet bruker
     */
    private fun setUpMapImagePins(style: Style){
        var icon = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.mapbox_marker_icon_default
        )

        val tag = "Legg til icon"

        if(icon == null) Log.d(tag, "RED")

        style.addImage(ICON_ID_RED, icon)


        icon = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.blue_marker
        )

        if(icon == null) Log.d(tag, "BLUE")
        style.addImage(ICON_ID_BLUE, icon)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

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
        val feature = mapFragmentViewModel.getFeature(place)
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
