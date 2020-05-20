package com.example.team11.ui.placesList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team11.database.entity.Place
import com.example.team11.R
import com.example.team11.database.entity.WeatherForecastDb
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
            Log.d("tagDatabaseStørrelsePlace", places.size.toString())
            Log.d("tagDatabasePrintList", places.toString())
            recycler_viewPlaces.layoutManager = layoutManager
            placesListViewModel.getForecasts(places)!!.observe(viewLifecycleOwner, Observer { forecasts ->
                Log.d("tagDatabaseStørrelseForecast", forecasts.size.toString())
                Log.d("tagDatabasePrintList", forecasts.toString())
                recycler_viewPlaces.adapter =
                    ListAdapter(
                        places,
                        forecasts,
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
                    search(text.toString(), places, forecasts)
                }
                parent_layout.setOnClickListener {
                    hideKeyboard()
                }
            })
        })

        placesListViewModel.personalPreference!!.observe(viewLifecycleOwner, Observer {})

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
    private fun search(name: String, places: List<Place>, forecasts: List<WeatherForecastDb>){
        filterPlaces = places.filter{ it.name.contains(name, ignoreCase = true)}
        recycler_viewPlaces.adapter =
            ListAdapter(
                filterPlaces,
                forecasts,
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

}
