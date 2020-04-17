package com.example.team11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team11.viewmodels.PlacesListActivityViewModel
import kotlinx.android.synthetic.main.activity_places_list.*



/*
 * PlacesListActivity viser cardsViews med informasjon om de forskjellige badeplassene
 */
class PlacesListActivity : AppCompatActivity() {

    private val viewModel: PlacesListActivityViewModel by viewModels{ PlacesListActivityViewModel.InstanceCreator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        val layoutManager = LinearLayoutManager(this)

        viewModel.places!!.observe(this, Observer { places ->
            recycler_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
            recycler_view.adapter = ListAdapter(places, this)

        })
    }
}



