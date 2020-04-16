package com.example.team11

import com.google.gson.annotations.SerializedName

data class Forecast(
    @SerializedName("mox:forecast")
    val ocenaforcasts: Array<Layer>
)

data class Layer(
    @SerializedName("metno:OceanForecast")
    val ocenaforcast: OceanForecast
)

data class OceanForecast(
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
