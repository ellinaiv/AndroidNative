package com.example.team11.ui.placesList

import androidx.test.rule.ActivityTestRule
import com.example.team11.ui.bottomNavigation.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import com.example.team11.Place


class PlacesListFragmentTes{

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testPlacesList(){

        val place = Place(1,"Sørenga",1.1,2.2, false,22,25)



    }

}