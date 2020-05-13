package com.example.team11.ui.placesList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team11.database.entity.Place
import com.example.team11.R
import com.example.team11.ui.filter.FilterActivity
import kotlinx.android.synthetic.main.fragment_places_list.*
import kotlinx.android.synthetic.main.fragment_places_list.searchText

class PlacesListFragment : Fragment() {

    private lateinit var placesListViewModel: PlacesListFragmentViewModel
    private lateinit var filterPlaces: List<Place>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO("Context i fragment kan være null før onAttach() og etter onDetach(), men burde være ganske safe i oncreat, litt usikker på om jeg burde bruke !! her.")
        placesListViewModel =
            ViewModelProvider(this, PlacesListFragmentViewModel.InstanceCreator(requireContext())).get(PlacesListFragmentViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_places_list, container, false)
        val layoutManager = LinearLayoutManager(context)

        placesListViewModel.places!!.observe(viewLifecycleOwner, Observer { places ->
            recycler_viewPlaces.layoutManager = layoutManager
            recycler_viewPlaces.adapter =
                ListAdapter(
                    places,
                    requireContext(),
                    placesListViewModel,
                    false
                )
            if (recycler_viewPlaces.adapter!!.itemCount == 0) {
                imageEmptyListShark.visibility = View.VISIBLE
                textNoElementInList.visibility = View.VISIBLE
            } else {
                imageEmptyListShark.visibility = View.GONE
                textNoElementInList.visibility = View.GONE
            }
            searchText.doOnTextChanged { text, _, _, _ ->
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
     * Søkefunksjonen filtrerer places etter navn og oppdaterer listen som vises på skjermen
     * @param name: en input-streng som skal brukes for å filtrere places
     * @param places: en liste med badesteder som skal filtreres
     */

    private fun search(name: String, places: List<Place>){
        filterPlaces = places.filter{ it.name.contains(name, ignoreCase = true)}
        recycler_viewPlaces.adapter =
            ListAdapter(
                filterPlaces,
                requireContext(),
                placesListViewModel,
                false
            )
        if (recycler_viewPlaces.adapter!!.itemCount == 0) {
            imageEmptyListShark.visibility = View.VISIBLE
            textNoElementInList.visibility = View.VISIBLE
        } else {
            imageEmptyListShark.visibility = View.GONE
            textNoElementInList.visibility = View.GONE
        }
    }

}
