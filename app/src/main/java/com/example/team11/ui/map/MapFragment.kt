package com.example.team11.ui.map

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.team11.database.entity.Place
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.R
import com.example.team11.ui.filter.FilterActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(), MapboxMap.OnMapClickListener {

    private val iconIdRed = "ICON_ID_RED"
    private val iconIdBlue = "ICON_ID_BLUE "
    private val geojsonId = "GEOJSON_ID"
    private val layerId = "LAYER_ID:"
    private var listOfLayerId = mutableListOf<String>()
    private val propertyId = "PROPERTY_ID"

    private var mapView: MapView? = null
    private lateinit var mapBoxMap: MapboxMap

    private lateinit var mapFragmentViewModel: MapFragmentViewModel

    private lateinit var filterPlaces: List<Place>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO("Context i fragment kan være null før onAttach() og etter onDetach(), men burde være ganske safe i oncreat, litt usikker på om jeg burde bruke !! her.")
        mapFragmentViewModel =
            ViewModelProvider(this, MapFragmentViewModel.InstanceCreator(requireContext())).get(MapFragmentViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapFragmentViewModel.places!!.observe(viewLifecycleOwner, Observer {places->
            makeMap(places)
            searchText.doOnTextChanged { text, _, _, _ ->
                if(text.toString().isEmpty()){
                    removePlace()
                }
                search(text.toString(), places)
            }
        })

        val filterButton = root.findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener {
            startActivity(Intent(this.requireContext(), FilterActivity::class.java))
        }

        return root
    }

    /**
     * Søkefunksjonen filtrerer places etter navn og zoomer til det stedet på kartet
     * @param name: en input-streng som skal brukes for å filtrere places
     * @param places: en liste med badesteder som skal filtreres
     */
    private fun search(name: String, places: List<Place>){
        filterPlaces = places.filter{ it.name.contains(name, ignoreCase = true)}
        if(filterPlaces.size == 1){
            val position = CameraPosition.Builder()
                .target(LatLng(filterPlaces[0].lat, filterPlaces[0].lng))
                .zoom(15.0)
                .build()
            mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)
            showPlace(filterPlaces[0])
        }
    }

    /**
     * Tegner opp kartet og passer på at alle steden blir plassert på kartet
     * @param places: en liste med steder som skal plasseres på kartet
     */
    private fun makeMap(places: List<Place>){
        mapView?.getMapAsync {mapBoxMap ->
            this.mapBoxMap = mapBoxMap
            mapBoxMap.setStyle(Style.MAPBOX_STREETS)

            mapBoxMap.getStyle { style ->
                removeLayers(style)
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

    /**
     * Fjerner alle steden på kartet
     * @param style: stilen til kartet
     */
    private fun removeLayers(style: Style){
        listOfLayerId.forEach { layer ->
            style.removeLayer(layer)
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
        val features = filterLayer(screenPoint)
        if(features.isNotEmpty()){
            val feature = features[0]
            val place = mapFragmentViewModel.places!!.value!!.filter {
                it.id == (feature.getNumberProperty(propertyId).toInt()) }[0]
            showPlace(place)
            return true
        }
        removePlace()
        return false
    }
    /**
     * Fjerner kortet som hviser informasjonen om et sted
     */
    private fun removePlace(){
        placeViewHolder.visibility = View.GONE
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
        when(mapFragmentViewModel.redWave(place)){
            true -> imageTempWater.setImageResource(R.drawable.water_red)
            false -> imageTempWater.setImageResource(R.drawable.water_blue)
        }
        textName.text = place.name
        textTempAir.text = getString(R.string.not_available)
        textTempWater.text = getString(R.string.tempC, place.tempWater)

        //zoomer til stedet på kartet
        val position = CameraPosition.Builder()
            .target(LatLng(place.lat, place.lng))
            .zoom(15.0)
            .build()
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)

        placeViewHolder.setOnClickListener{
            mapFragmentViewModel.changeCurrentPlace(place)
            val intent = Intent(requireContext(), PlaceActivity::class.java)
            startActivity(intent)
        }
        placeViewHolder.visibility = View.VISIBLE
    }

    /**
     * Legger til ikoner som man kan plassere på kartet
     * @param style:  stilen som kartet bruker
     */
    private fun setUpMapImagePins(style: Style){
        var icon = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.marker_red
        )
        style.addImage(iconIdRed, icon)

        icon = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.marker_blue
        )
        style.addImage(iconIdBlue, icon)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)

    }
    /**
     * Legger til en marker for en place. Legger til farge på markeren etter Preferance og
     * legger til navnet på stedet
     *
     * @param place: Stedet som skal plasseres på kartet
     * @param style: Stilen på kartet
     */
    private fun addMarker(place: Place, style: Style){
        val id = layerId + place.id.toString()
        val geoId = geojsonId + place.id.toString()
        val feature = mapFragmentViewModel.getFeature(place)
        feature.addNumberProperty(propertyId, place.id)
        val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
            arrayListOf(feature)))
        style.addSource(geoJsonSource)

        val iconId = when(mapFragmentViewModel.isPlaceWarm(place)){
            true -> iconIdRed
            false -> iconIdBlue
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
