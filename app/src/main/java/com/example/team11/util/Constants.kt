package com.example.team11.util

object Constants {

    // Navn på databaser
    const val MEATDATA_ENTRY_PLACE_TABLE = "place"
    const val METADATA_ENTRY_WEATHER_FORECAST_TABLE = "weather_forecast"

    // Antall timer som vises i place activity
    const val NUMB_HOURS_FORECAST = 6
    const val NUMB_DAYS_FORECAST = 6

    // Maks og min verdi for mulig preferanse på vann- og lufttemperatur
    const val waterTempLow: Int = 0
    const val waterTempHigh: Int = 30
    const val airTempLow: Int = -15
    const val airTempHigh: Int = 40
}
