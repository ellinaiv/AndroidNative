package com.example.team11.ui.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team11.ui.placesList.ListAdapter
import com.example.team11.R
import kotlinx.android.synthetic.main.fragment_favorites.*
import com.example.team11.database.entity.Place


class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutManager = LinearLayoutManager(context)
        viewModel =
            ViewModelProvider(this, FavoritesFragmentViewModel.InstanceCreator(requireContext())).get(FavoritesFragmentViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        if(viewModel.favoritePlaces != null){
            Transformations.switchMap(viewModel.favoritePlaces!!){places ->
                viewModel.getForecasts(places)
            }.observe(viewLifecycleOwner, Observer {forecasts ->
                val favoritePlaces = viewModel.favoritePlaces!!.value ?: emptyList()
                recycler_view.layoutManager = layoutManager
                recycler_view.adapter =
                    ListAdapter(favoritePlaces, forecasts, requireContext(), viewModel, true)
                if (recycler_view.adapter!!.itemCount == 0) {
                    imageEmptyListShark.visibility = View.VISIBLE
                    textNoElementInList.visibility = View.VISIBLE
                } else {
                    imageEmptyListShark.visibility = View.GONE
                    textNoElementInList.visibility = View.GONE
                }
            })
        }
        viewModel.personalPreference.observe(viewLifecycleOwner, Observer { })

        return root
    }
}
