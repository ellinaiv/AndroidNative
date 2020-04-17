package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team11.viewmodels.PlacesListActivityViewModel
import kotlinx.android.synthetic.main.activity_places_list.*



/*
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
            recycler_view.layoutManager = layoutManager
            recycler_view.adapter = ListAdapter(places, this)
            val searchBar = findViewById<EditText>(R.id.searchText)
            /*
             * Søkefunksjonen filtrerer places etter navn og oppdaterer listen som vises på skjermen
             */
            searchBar.doOnTextChanged { text, _, _, _ ->
                filterPlaces = places.filter{ it.name.contains(text.toString(), ignoreCase = true)}
                recycler_view.adapter = ListAdapter(filterPlaces, this)
            }
        })
    }
}
