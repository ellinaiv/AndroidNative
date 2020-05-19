package com.example.team11.ui.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources.getDrawable
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
    private val iconIdBlue = "ICON_ID_BLUE"
    private val iconIdGray = "ICON_ID_GRAY"
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
                if (text.toString().isEmpty()) {
                    removePlace()
                }
                search(text.toString(), places)
            }
        })
        //TODO("EHm denne gjør ingenting, men trneger den for at pp ikke ksal vøre null i viewmodel, dette kan umulig være riktige måte å gjøre det på...")
        mapFragmentViewModel.personalPreference!!.observe(viewLifecycleOwner, Observer {  })

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
    private fun search(name: String, places: List<Place>) {
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
        hideKeyboard()
        return handleClickIcon(mapBoxMap.projection.toScreenLocation(point))
    }

    /**
     * Det er denne metoden som registrerer om trykket faktisk traff et punkt på kartet, eller
     * ikke, og hva man i så fall skal gjøre når man har trykket på noe. Her vil den da vise
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
     * Fjerner kortet som viser informasjonen om et sted
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
    @SuppressLint("UseRequireInsteadOfGet")
    private fun showPlace(place: Place){
        Log.d("tagPlace", place.toString())
        textName.text = place.name
        if(place.tempWater != Int.MAX_VALUE) {
            when (mapFragmentViewModel.redWave(place)) {
                true -> imageTempWater.setImageResource(R.drawable.water_red)
                false -> imageTempWater.setImageResource(R.drawable.water_blue)
            }
            textTempWater.text = getString(R.string.tempC, place.tempWater)
        } else {
            imageTempWater.setImageResource(R.drawable.ic_nodatawave)
            textTempWater.text = getString(R.string.no_data)
        }

        imageTempAir.setImageDrawable(getDrawable(this@MapFragment.context!!, R.drawable.ic_noweatherdata))
        textTempAir.text = getString(R.string.no_data)
        mapFragmentViewModel.getNowForcast(place)?.observe(viewLifecycleOwner, Observer {forecast ->
            if(forecast != null && forecast.isNotEmpty()){
                val placeForecast = forecast[0]
                if(placeForecast.placeId == place.id && placeForecast.tempAir != Int.MAX_VALUE){
                    textTempAir.text = getString(R.string.tempC, placeForecast.tempAir)
                    imageTempAir.setImageDrawable(getDrawable(this@MapFragment.context!!, resources.getIdentifier(placeForecast.symbol,
                        "drawable", activity!!.packageName)))
                }
            }
        })


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

        icon = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.marker_no_data
        )
        style.addImage(iconIdGray, icon)
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


        var iconId = when(mapFragmentViewModel.isPlaceWarm(place)){
            true -> iconIdRed
            false -> iconIdBlue
        }

        if(mapFragmentViewModel.isPinGray(place)){
            iconId = iconIdGray
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


    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
