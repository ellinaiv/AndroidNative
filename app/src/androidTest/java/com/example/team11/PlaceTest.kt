package com.example.team11

import org.junit.Assert.*
import org.junit.Test

class PlaceTest{

    private var midTemp = 25
    private var minTemp = 15

    @Test
    fun testPreferenceCheck1(){
        val place = Place(-1 , "test", 10.toDouble(),10.toDouble(), 25)
        assertEquals(Preference.OPTIMAL, place.preferenceCheck(minTemp, midTemp))
    }

    @Test
    fun testPreferenceCheck2(){
        val place = Place(-1 , "test", 10.toDouble(),10.toDouble(), 15)
        assertEquals(Preference.OKEY, place.preferenceCheck(minTemp, midTemp))
    }


    @Test
    fun testPreferenceCheck3(){
        val place = Place(-1 , "test", 10.toDouble(),10.toDouble(), 14)
        assertEquals(Preference.NOT_OKEY, place.preferenceCheck(minTemp, midTemp))
    }
}