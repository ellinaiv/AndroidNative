package com.example.team11.api.valueObjects

import com.google.gson.annotations.SerializedName

data class OceanForecast(
    @SerializedName("mox:forecast")
    val OceanForecastLayers: List<Layer>
)

data class Layer(
    @SerializedName("metno:OceanForecast")
    val OceanForecastDetails: OceanForecastDetails
)

data class OceanForecastDetails(
    @SerializedName("mox:seaCurrentSpeed")
    val seaSpeed: SeaSpeed,

    @SerializedName("mox:validTime")
    val validTime: ValidTime
)

data class SeaSpeed(val uom: String, val content: String)

data class ValidTime(
    @SerializedName("gml:TimePeriod")
    val timePeriod: TimePeriod
)

data class TimePeriod(
    @SerializedName("gml:begin")
    val time: String
)