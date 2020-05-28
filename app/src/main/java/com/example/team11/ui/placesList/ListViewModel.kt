package com.example.team11.ui.placesList

import com.example.team11.Color
import com.example.team11.database.entity.Place

interface ListViewModel {
    fun colorWave(place: Place): Color
    fun changeCurrentPlace(place: Place)
}