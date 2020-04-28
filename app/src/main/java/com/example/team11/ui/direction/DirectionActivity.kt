package com.example.team11.ui.direction

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var mapboxMap: MapboxMap
    private var mapView: MapView? = null
    private val ROUTE_SOURCE_ID = "ROUTE_SOURCE_ID"
    private var way: Transportation? = null
    private var tag = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_direction)
        supportActionBar!!.hide()
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        //Observerer stedet som er valgt
        viewModel.place!!.observe(this, Observer { place ->
            viewModel.wayOfTransportation!!.observe(this, Observer { way->
                this.way = way
                makeMap(place, savedInstanceState)
            })
        })
    }

    /**
     * Lager kartet, tegner opp destionasjon og lokasjon. Viser rute, og zoomer inn på destinasjon
     * @param place: Stedet som skal vises på kartet
     * @param savedInstanceState: mapView trenger denne til onCreate metoden sin
     */
    private fun makeMap(place: Place, savedInstanceState: Bundle?) {
        Log.d(tag, "makemap")
        mapView = findViewById<MapView>(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS)
            mapboxMap.getStyle { style ->
                style.addSource(GeoJsonSource(ROUTE_SOURCE_ID))
                makeRouteLayer(style)
                addDestinationMarker(place, style)
                val position = CameraPosition.Builder()
                    .target(LatLng(place.lat, place.lng))
                    .zoom(10.0)
                    .build()
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2)
            }
        }
        Log.d(tag, "makemapDONE")
    }

    /**
     * Lager et layer på kartet, slik at man kan tegne det opp på et senere tidspunkt
     * @param style: stilen kartet skal tegnes oppå
     */
    private fun makeRouteLayer(style: Style){
        enableLocationComponent(style)
        val routeLayer = LineLayer("ROUTE_LAYER_ID", ROUTE_SOURCE_ID)

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

    /**
     * Lager ruten, og tegner den på kartet
     * @param place: stedet, osm er destinasjonen
     */
    private fun getRoute(place: Place){
        //hentet stedet vi skal bruke
        Log.d(tag, "getPlace")
        val originLocation = mapboxMap.locationComponent.lastKnownLocation ?: return
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

                val stringD = "Lengde: " + viewModel.convertToCorrectDistance(currentRoute.distance())
                val stringT = "\nTid: " + viewModel.convertTime(currentRoute.duration())

                textTitleRoute.text = getString(R.string.titleRoute, place.name)
                textDistance.text = viewModel.convertToCorrectDistance(currentRoute.distance())
                textTime.text = viewModel.convertTime(currentRoute.duration())
                layoutAboutRoute.visibility = View.VISIBLE



                mapboxMap.getStyle { style ->
                    val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
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

            mapboxMap.locationComponent.apply {
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
            enableLocationComponent(mapboxMap.style!!)
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
