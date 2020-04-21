package com.example.team11

import com.google.gson.annotations.SerializedName
import java.sql.Time

// TODO("Burde disse ligge i SeaCurrent og vi kaller den APIModels? Eller burde vi har to ulike filer?")

data class WeatherForecast(
    @SerializedName("product")
    val weatherForecast: WeatherForecastLayer
)

data class WeatherForecastLayer(
    @SerializedName("time")
    val weatherForecastTimeSlot: Array<WeatherForecastTimeSlot>
)

data class WeatherForecastTimeSlot(
    @SerializedName("to")
    val toTime: String,

    @SerializedName("from")
    val fromTime: String,

    @SerializedName("location")
    val forecastInfo: LocationForecast
)

data class LocationForecast(
    @SerializedName("temperature")
    val temp: WeatherTemp
)

data class WeatherTemp(val unit: String, val value: String, val id: String)