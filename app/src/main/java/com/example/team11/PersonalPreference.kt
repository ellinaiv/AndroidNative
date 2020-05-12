package com.example.team11

import com.example.team11.database.entity.Place


class PersonalPreference(
    val waterTempLow: Int = 0,
    var waterTempMid : Int = 15,
    val waterTempHigh: Int = 30,
    val airTempLow: Int = -30,
    var airTempMid : Int = 10,
    val airTempHigh: Int = 30,
    var showWaterCold: Boolean = true,
    var showWaterWarm: Boolean = true,
    var showAirCold: Boolean = true,
    var showAirWarm: Boolean = true,
    var showBasedOnWater: Boolean = true
){
     /**
      * Sjekker om et gitt sted har riktig kriterier mtp vanntempratur for å vises.
      * Hvis det ikke er noe data vil stedet fortsatt vises.
      * @param place stedet som skal sjekkes
      * @return true hvis den oppfyller kriteriene false ellers
      */
     fun isTempWaterOk(place: Place): Boolean{
         if(place.tempWater == Int.MAX_VALUE) return true
         return ((showWaterWarm and (place.tempWater >= waterTempMid))
                 or (showWaterCold and (place.tempWater < waterTempMid)))
     }

     /**
      * Sjekker om et gitt sted har riktig kriterier mtp luftempratur for å vises
      * Hvis det ikke er noe data vil stedet fortsatt vises.
      * @param place stedet som skal sjekkes
      * @return true hvis den oppfyller kriteriene false ellers
      */
     fun isTempAirOk(place: Place): Boolean{
         if(place.tempAir == Int.MAX_VALUE) return true
         return ((showAirWarm and (place.tempAir >= airTempMid))
                 or (showAirCold and (place.tempAir < airTempMid)))
     }
 }
