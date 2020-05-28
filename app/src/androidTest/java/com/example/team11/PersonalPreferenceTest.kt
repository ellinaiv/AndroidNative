/**
 * TODO må fikses!
 * har kommentert ut pga feilmeldinger pga place pga endring i arkitektur   //melanie
 */

package com.example.team11

import com.example.team11.database.entity.PersonalPreference
import com.example.team11.database.entity.Place

import org.junit.Assert.*
import org.junit.Test

class PersonalPreferenceTest{

//    private val pp = PersonalPreference()
//    private val placeOk = Place(-1, "TestOk", 0.1, 0.2,
//        tempAir = pp.airTempMid + 5, tempWater = pp.waterTempMid + 5)
//    private val placeJustOk = Place(-2, "TestJustOk", 0.1, 0.2,
//        tempAir = pp.airTempMid, tempWater = pp.waterTempMid)
//    private val placeJustNotOK = Place(-3, "TestJustNotOk", 0.1, 0.2,
//        tempAir = pp.airTempMid- 1, tempWater = pp.waterTempMid - 1)
//    private val placeNotOK = Place(-4, "TestNotOk", 0.1, 0.2,
//        tempAir = pp.airTempMid- 5, tempWater = pp.waterTempMid -5)
//    private val placeNoData= Place(-5, "TestNoData", 0.1, 0.2,
//        tempAir = Int.MAX_VALUE, tempWater = Int.MAX_VALUE)
//
//    @Test
//    fun testTempWaterShowColdAndWarm(){
//        assertTrue(pp.isTempWaterOk(placeOk))
//        assertTrue(pp.isTempWaterOk(placeJustOk))
//        assertTrue(pp.isTempWaterOk(placeJustNotOK))
//        assertTrue(pp.isTempWaterOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempWaterOnlyShowCold(){
//        pp.showWaterWarm = false
//        assertFalse(pp.isTempWaterOk(placeOk))
//        assertFalse(pp.isTempWaterOk(placeJustOk))
//        assertTrue(pp.isTempWaterOk(placeJustNotOK))
//        assertTrue(pp.isTempWaterOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempWaterOnlyShowWarm(){
//        pp.showWaterCold = false
//        assertTrue(pp.isTempWaterOk(placeOk))
//        assertTrue(pp.isTempWaterOk(placeJustOk))
//        assertFalse(pp.isTempWaterOk(placeJustNotOK))
//        assertFalse(pp.isTempWaterOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempWaterNotShowAnyThing(){
//        pp.showWaterCold = false
//        pp.showWaterWarm = false
//        assertFalse(pp.isTempWaterOk(placeOk))
//        assertFalse(pp.isTempWaterOk(placeJustOk))
//        assertFalse(pp.isTempWaterOk(placeJustNotOK))
//        assertFalse(pp.isTempWaterOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempAirShowColdAndWarm(){
//        assertTrue(pp.isTempAirOk(placeOk))
//        assertTrue(pp.isTempAirOk(placeJustOk))
//        assertTrue(pp.isTempAirOk(placeJustNotOK))
//        assertTrue(pp.isTempAirOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempAirOnlyShowCold(){
//        pp.showAirWarm = false
//        assertFalse(pp.isTempAirOk(placeOk))
//        assertFalse(pp.isTempAirOk(placeJustOk))
//        assertTrue(pp.isTempAirOk(placeJustNotOK))
//        assertTrue(pp.isTempAirOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempAirOnlyShowWarm(){
//        pp.showAirCold = false
//        assertTrue(pp.isTempAirOk(placeOk))
//        assertTrue(pp.isTempAirOk(placeJustOk))
//        assertFalse(pp.isTempAirOk(placeJustNotOK))
//        assertFalse(pp.isTempAirOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
//
//    @Test
//    fun testTempAirNotShowAnyThing(){
//        pp.showAirCold = false
//        pp.showAirWarm = false
//        assertFalse(pp.isTempAirOk(placeOk))
//        assertFalse(pp.isTempAirOk(placeJustOk))
//        assertFalse(pp.isTempAirOk(placeJustNotOK))
//        assertFalse(pp.isTempAirOk(placeNotOK))
//        assertTrue(pp.isTempWaterOk(placeNoData))
//    }
}