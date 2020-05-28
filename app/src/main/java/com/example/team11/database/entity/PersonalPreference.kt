package com.example.team11.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Klasse med entity for personal_preference databasen.
 */

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
    override fun toString(): String {
        return "PersonalPreference(waterTempMid=$waterTempMid, airTempMid=$airTempMid, showWaterCold=$showWaterCold, showWaterWarm=$showWaterWarm, showAirCold=$showAirCold, showAirWarm=$showAirWarm, showBasedOnWater=$showBasedOnWater, falseData=$falseData, id=$id)"
    }

}
