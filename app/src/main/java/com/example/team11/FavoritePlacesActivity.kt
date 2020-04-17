package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.viewmodels.FavoritePlacesActivityViewModel
import com.example.team11.viewmodels.PlacesListActivityViewModel
import kotlinx.android.synthetic.main.activity_places_list.*

class FavoritePlacesActivity : AppCompatActivity() {

    private val viewModel: FavoritePlacesActivityViewModel by viewModels{ FavoritePlacesActivityViewModel.InstanceCreator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_places)
        val layoutManager = LinearLayoutManager(this)

        viewModel.places!!.observe(this, Observer { places ->
            val favoritePlaces = places.filter { place -> place.favorite }
            recycler_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
            recycler_view.adapter = ListAdapter(favoritePlaces, this)

        })
    }
}
