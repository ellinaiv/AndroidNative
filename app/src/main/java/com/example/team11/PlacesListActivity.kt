package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.viewmodels.PlacesListActivityViewModel
import kotlinx.android.synthetic.main.activity_places_list.*



/**
 * PlacesListActivity viser cardsViews med informasjon om de forskjellige badeplassene
 */
class PlacesListActivity : AppCompatActivity() {

    private val viewModel: PlacesListActivityViewModel by viewModels{ PlacesListActivityViewModel.InstanceCreator() }
    private lateinit var filterPlaces: List<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        val layoutManager = LinearLayoutManager(this)

        viewModel.places!!.observe(this, Observer { places ->
            recycler_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
            recycler_view.adapter = ListAdapter(places, this, viewModel, false)

            recycler_view.layoutManager = layoutManager
            recycler_view.adapter = ListAdapter(places, this)
            val searchBar = findViewById<EditText>(R.id.searchText)
            searchBar.doOnTextChanged { text, _, _, _ ->
                search(text.toString(), places)
            }
        })
    }

    /**
     * Søkefunksjonen filtrerer places etter navn og oppdaterer listen som vises på skjermen
     * @param text: en input-streng som skal brukes for å filtrere places
     * @param places: en liste med badesteder som skal filtreres
     */
    private fun search(name: String, places: List<Place>){
        filterPlaces = places.filter{ it.name.contains(name.toString(), ignoreCase = true)}
        recycler_view.adapter = ListAdapter(filterPlaces, this)
    }
}



