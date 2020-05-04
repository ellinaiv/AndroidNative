package com.example.team11


data class PersonalPreference(
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
)
