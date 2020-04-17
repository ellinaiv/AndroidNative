package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_places_list.*



/*
 * PlacesListActivity viser cardsViews med informasjon om de forskjellige badeplassene
 */
class PlacesListActivity : AppCompatActivity() {

    private lateinit var filterPlaces: List<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        val layoutManager = LinearLayoutManager(this)

        val places = intent.getSerializableExtra("PLACES_LIST") as ArrayList<Place>
        recycler_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
        recycler_view.adapter = ListAdapter(places, this)

        val searchBar = findViewById<EditText>(R.id.searchText)

        searchBar.doOnTextChanged { text, _, _, _ ->
            filterPlaces = places.filter{ it.name.contains(text.toString(), ignoreCase = true)}
            recycler_view.adapter = ListAdapter(filterPlaces, this)
        }

    }
}



