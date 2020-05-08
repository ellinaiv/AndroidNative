package com.example.team11


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
      * Sjekker om et gitt sted har riktig kriterier mtp vanntempratur for å vises
      * @param place stedet som skal sjekkes
      * @return true hvis den oppfyller kriteriene false ellers
      */
     fun isTempWaterOk(place: Place): Boolean{
         //TODO: Legge til tilfellet for vi ikke har noe data (da skal den bli true)
         return ((showWaterWarm and (place.tempWater >= waterTempMid))
                 or (showWaterCold and (place.tempWater < waterTempMid)))
     }

     /**
      * Sjekker om et gitt sted har riktig kriterier mtp luftempratur for å vises
      * @param place stedet som skal sjekkes
      * @return true hvis den oppfyller kriteriene false ellers
      */
     fun isTempAirOk(place: Place): Boolean{
         //TODO: Legge til tilfellet for vi ikke har noe data (da skal den bli true)
         return ((showAirWarm and (place.tempAir >= airTempMid))
                 or (showAirCold and (place.tempAir < airTempMid)))
     }
 }
