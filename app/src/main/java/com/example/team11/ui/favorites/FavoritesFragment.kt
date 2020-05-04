package com.example.team11.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team11.ui.placesList.ListAdapter
import com.example.team11.R
import com.example.team11.viewmodels.FavoritesFragmentViewModel
import kotlinx.android.synthetic.main.activity_places_list.*

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: FavoritesFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutManager = LinearLayoutManager(context)
        viewModel =
            ViewModelProvider(this, FavoritesFragmentViewModel.InstanceCreator(context!!)).get(FavoritesFragmentViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        viewModel.favoritePlaces!!.observe(viewLifecycleOwner, Observer { favoritePlaces ->
            recycler_view.layoutManager = layoutManager
            recycler_view.adapter = ListAdapter(
                favoritePlaces,
                context!!,
                viewModel,
                true
            )
        })

        return root
    }
}
