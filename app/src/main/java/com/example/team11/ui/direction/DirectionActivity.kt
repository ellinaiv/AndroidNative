package com.example.team11.ui.direction

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.team11.Place
import com.example.team11.R
import com.example.team11.Transportation
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_direction.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectionActivity : AppCompatActivity() , PermissionsListener {

    private val viewModel: DirectionActivityViewModel by viewModels{ DirectionActivityViewModel.InstanceCreator() }
    private var permissionManager = PermissionsManager(this)
    private var mapboxMap: MapboxMap? = null
    private var mapView: MapView? = null
    private val routeSourceId = "ROUTE_SOURCE_ID"
    private var way: Transportation? = null
    private var tag = "TAG"
    private var buttonWalkClicked = false
    private var buttonBikeClicked = false
    private var buttonCarClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_direction)
        supportActionBar!!.hide()

        buttonBack.setOnClickListener {
            finish()
        }

        buttonTransportationEvents()
        var first = true

        //Observerer stedet som er valgt
        viewModel.place!!.observe(this, Observer { place ->
            makeMap(place, savedInstanceState)
            makeTitleText(place)
            viewModel.wayOfTransportation!!.observe(this, Observer { way->
                this.way = way
                if(first){
                    first = false
                    when(way!!){
                        Transportation.WALK -> {
                            buttonWalk.setImageResource(R.drawable.directions_walk_pink)
                            buttonWalk.setBackgroundResource(R.drawable.background)
                        }
                        Transportation.BIKE->{
                            buttonBike.setImageResource(R.drawable.directions_bike_pink)
                            buttonBike.setBackgroundResource(R.drawable.background)
                        }
                        Transportation.CAR->{
                            buttonCar.setImageResource(R.drawable.directions_car_pink)
                            buttonCar.setBackgroundResource(R.drawable.background)
                        }
                    }
                }else{
                    makeRoute()
                }
                buttonRefresh.setOnClickListener {
                    makeRoute()
                }
            })
        })
    }

    /**
     * Setter evenetene til de ulike transport-knappene
     */
    private fun buttonTransportationEvents(){
        buttonWalk.setOnClickListener {
            if(! buttonWalkClicked){
                resetTransportationButtons()
                buttonWalkClicked = true
                buttonWalk.setImageResource(R.drawable.directions_walk_pink)
                buttonWalk.setBackgroundResource(R.drawable.background)
                viewModel.changeWayOfTransportation(Transportation.WALK)
            }
        }

        buttonBike.setOnClickListener {
            if(!buttonBikeClicked){
                resetTransportationButtons()
                buttonBikeClicked = true
                buttonBike.setImageResource(R.drawable.directions_bike_pink)
                buttonBike.setBackgroundResource(R.drawable.background)
                viewModel.changeWayOfTransportation(Transportation.BIKE)
            }
        }

        buttonCar.setOnClickListener {
            if(! buttonCarClicked){
                resetTransportationButtons()
                buttonCarClicked = true
                buttonCar.setImageResource(R.drawable.directions_car_pink)
                buttonCar.setBackgroundResource(R.drawable.background)
                viewModel.changeWayOfTransportation(Transportation.CAR)
            }
        }
    }

    /**
     * Setter alle transport-knappene tilbake til sin orginale tilstand (mtp. design)
     */
    private fun resetTransportationButtons(){
        buttonWalk.setBackgroundColor(resources.getColor(R.color.pinkIconColor, null))
        buttonBike.setBackgroundColor(resources.getColor(R.color.pinkIconColor, null))
        buttonCar.setBackgroundColor(resources.getColor(R.color.pinkIconColor, null))

        buttonWalk.setImageResource(R.drawable.directions_walk_white)
        buttonBike.setImageResource(R.drawable.directions_bike_white)
        buttonCar.setImageResource(R.drawable.directions_car_white)

        buttonWalkClicked = false
        buttonBikeClicked = false
        buttonCarClicked = false
    }

    /**
     * Sammen med enableButtons brukes denne funksjon som en måte å bregense kall på mapbox,
     * så man får kun lov til å trykk når ett annet kall ikke kjører
     */
    private fun disableButtons(){
        buttonWalk.isEnabled = false
        buttonBike.isEnabled = false
        buttonCar.isEnabled = false
        buttonRefresh.isEnabled = false
    }

    /**
     * Sammen med disaableButtons brukes denne funksjon som en måte å bregense kall på mapbox,
     * så man får kun lov til å trykk når ett annet kall ikke kjører
     */
    private fun enableButtons(){
        buttonWalk.isEnabled = true
        buttonBike.isEnabled = true
        buttonCar.isEnabled = true
        buttonRefresh.isEnabled = true
    }


    /**
     * Lager kartet, tegner opp destionasjon og lokasjon. Viser rute, og zoomer inn på destinasjon
     * @param place: Stedet som skal vises på kartet
     * @param savedInstanceState: mapView trenger denne til onCreate metoden sin
     */
    private fun makeMap(place: Place, savedInstanceState: Bundle?) {
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            disableButtons()
            this.mapboxMap = mapboxMap
            Log.d(tag, mapboxMap.toString())
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
            try {
                mapboxMap.getStyle { style ->
                    style.addSource(GeoJsonSource(routeSourceId))
                    makeRouteLayer(style)
                    addDestinationMarker(place, style)
                    val position = CameraPosition.Builder()
                        .target(LatLng(place.lat, place.lng))
                        .zoom(10.0)
                        .build()
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)
                    enableLocationComponent(style)
                }
            }finally {
                enableButtons()
            }
        }
    }

    /**
     * Har ansvar for å starte prosessen for å lage rute.
     */
    private fun makeRoute(){
        mapView?.getMapAsync {
            try{
                disableButtons()
                it.getStyle {style ->
                    enableLocationComponent(style)
                }
            }finally {
                enableButtons()
            }
        }?: Toast.makeText(this, getString(R.string.noMap) , Toast.LENGTH_SHORT).show()
    }

    /**
     * Lager et layer på kartet, slik at man kan tegne det opp på et senere tidspunkt
     * @param style: stilen kartet skal tegnes oppå
     */
    private fun makeRouteLayer(style: Style){
        val routeLayer = LineLayer("ROUTE_LAYER_ID", routeSourceId)
        routeLayer.setProperties(
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
            PropertyFactory.lineWidth(3f),
            PropertyFactory.lineColor(ContextCompat
                .getColor(this, R.color.pinkIconColor))
        )
        style.addLayer(routeLayer)
    }

    /**
     * Legger til pinnen til destinasjonen
     * @param place: stedet som er destinasjonen
     * @param style: stilen pinnen skla plaseres på
     */
    private fun addDestinationMarker(place: Place, style: Style){
        val iconIdRed = "ICON_ID_RED"
        val geoId = "GEO_ID"
        val icon = BitmapFactory.decodeResource(
            this@DirectionActivity.resources,
            R.drawable.marker_place
        )
        style.addImage(iconIdRed, icon)

        val feature = viewModel.getFeature(place)
        val geoJsonSource = GeoJsonSource(geoId, FeatureCollection.fromFeatures(
            arrayListOf(feature)))
        style.addSource(geoJsonSource)

        val symbolLayer = SymbolLayer("SYMBOL_LAYER_ID", geoId)
        symbolLayer.withProperties(
            PropertyFactory.iconImage(iconIdRed),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        style.addLayer(symbolLayer)
    }

    /**
     * Lager tittelen til reiseveien, og legger til tykk skrift
     * @param place stedet som er destinasjonen
     */
    private fun makeTitleText(place: Place){
        val aboutDirection = SpannableStringBuilder(" " + getString(R.string.yourPosition) + " ")
        aboutDirection.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            aboutDirection.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val destination = SpannableStringBuilder(" " + place.name)
        destination.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            destination.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        aboutDirection.insert(0, getString(R.string.from))
        aboutDirection.insert(aboutDirection.length, getString(R.string.to))
        aboutDirection.insert(aboutDirection.length, destination)

        textTitleRoute.text =  aboutDirection
    }

    /**
     * Lager ruten, og tegner den på kartet
     * @param place: stedet, osm er destinasjonen
     */
    private fun getRoute(place: Place){
        //hentet stedet vi skal bruke
        Log.d(tag, "getPlace")
        val originLocation = mapboxMap!!.locationComponent.lastKnownLocation ?: return
        val originPoint = Point.fromLngLat(originLocation.longitude, originLocation.latitude)
        val profile = when(way){
            Transportation.BIKE -> DirectionsCriteria.PROFILE_CYCLING
            Transportation.CAR -> DirectionsCriteria.PROFILE_DRIVING
            Transportation.WALK -> DirectionsCriteria.PROFILE_WALKING
            else -> DirectionsCriteria.PROFILE_CYCLING
        }

        //lager en klient som er ansvarlig for alt rundt ruten
        val client = MapboxDirections.builder()
            .origin(originPoint)
            .destination(Point.fromLngLat(place.lng, place.lat))
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(profile)
            .accessToken(getString(R.string.access_token))
            .steps(false)
            .build()


        //tar ansvar for å tegne opp selve ruta
        client.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if(response.body() == null || response.body()!!.routes().size < 1){
                    Toast.makeText(this@DirectionActivity, "No routes found", Toast.LENGTH_SHORT).show()
                    return
                }

                val currentRoute = response.body()!!.routes()[0]

                textDistance.text = viewModel.convertToCorrectDistance(currentRoute.distance())
                textTime.text = viewModel.convertTime(currentRoute.duration())
                layoutAboutRoute.visibility = View.VISIBLE



                mapboxMap!!.getStyle { style ->
                    val source = style.getSourceAs<GeoJsonSource>(routeSourceId)
                    source?.setGeoJson(
                        LineString.fromPolyline(currentRoute.geometry()!!,
                            Constants.PRECISION_6
                        ))
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Toast.makeText(this@DirectionActivity, "Error: " + t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    /**
     * Finner lokasjonen til brukeren og bestemmer hvordan stilen runt bruker punktet skal være
     * @param style: stilen som skal tegnes opp
     */
    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(style: Style){
        Log.d(tag, "enableLocationComponent")
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            //grunnet et problem med emulatorer vil ikke foregroundDrawable bli svart på kartet,
            //skal fungere som normalt på et vanlig device
            val customLocationComponentOptions =
                LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .foregroundDrawable(R.drawable.start_position)
                    .accuracyColor(ContextCompat.getColor(this@DirectionActivity,
                        R.color.colorPrimary
                    ))
                    .build()

            val locationComponentActivityOptions =
                LocationComponentActivationOptions.builder(this, style)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            mapboxMap!!.locationComponent.apply {
                activateLocationComponent(locationComponentActivityOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.NORMAL
            }
            getRoute(viewModel.place!!.value!!)
        }else{
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, getString(R.string.viTrengerPosFordi), Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableLocationComponent(mapboxMap!!.style!!)
            getRoute(viewModel.place!!.value!!)
        }else{
            Toast.makeText(this, getString(R.string.ikkeViseVei), Toast.LENGTH_LONG).show()
            finish()
        }
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
