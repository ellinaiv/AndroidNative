package com.example.team11.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_preference")
class PersonalPreference(
    var waterTempMid : Int = 15,
    var airTempMid : Int = 10,
    var showWaterCold: Boolean = true,
    var showWaterWarm: Boolean = true,
    var showAirCold: Boolean = true,
    var showAirWarm: Boolean = true,
    var showBasedOnWater: Boolean = true,
    var falseData: Boolean = false,
    @PrimaryKey() val id: Int = 0
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
    //TODO("This:")
    /* fun isTempAirOk(place: Place): Boolean{
         if(place.tempAir == Int.MAX_VALUE) return true
         return ((showAirWarm and (place.tempAir >= airTempMid))
                 or (showAirCold and (place.tempAir < airTempMid)))
     }*/
 }
