package com.example.team11.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.team11.R

class MapFragment : Fragment() {

    private lateinit var mapFragmentViewModel: MapFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapFragmentViewModel =
            ViewModelProviders.of(this).get(MapFragmentViewModel::class.java)
        //TODO: change to map when wifi gets stable    ****
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        mapFragmentViewModel.places!!.observe(viewLifecycleOwner, Observer {
        })
        return root
    }
}
