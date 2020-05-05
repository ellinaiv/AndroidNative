package com.example.team11.depricated

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.ui.placesList.ListAdapter
import com.example.team11.R
import kotlinx.android.synthetic.main.activity_places_list.*

class FavoritePlacesActivity : AppCompatActivity() {

    private val viewModel: FavoritePlacesActivityViewModel by viewModels{ FavoritePlacesActivityViewModel.InstanceCreator() }

    /**
     * FavoritePlacesListActivity viser cardsViews med informasjon om favoritt badeplassene
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_places)
        val layoutManager = LinearLayoutManager(this)

        viewModel.favoritePlaces!!.observe(this, Observer { places ->
            recycler_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
            recycler_view.adapter =
                ListAdapter(
                    places,
                    this,
                    viewModel,
                    true
                )

        })
    }
}
