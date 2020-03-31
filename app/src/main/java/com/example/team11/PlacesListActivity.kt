package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class PlacesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        val places = intent.getSerializableExtra("PLACES_LIST")  as ArrayList<Place>
        /*
        * manuelt testing for badesteder, skal slettes
        */
        for(place in places){
            Log.d("heeei", "placesList")
            Log.d("name: ", place.name)
            Log.d("LatLng: ", place.getLatLng().toString())
            Log.d("tmp: ", place.temp.toString())
        }

    }
}
