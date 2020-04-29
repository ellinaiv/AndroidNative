package com.example.team11.valueObjects

import com.google.gson.annotations.SerializedName

// TODO("Burde disse ligge i SeaCurrent og vi kaller den APIModels? Eller burde vi har to ulike filer?")


// For versjon 1.9 av apiet
/*
data class WeatherForecast(
    @SerializedName("product")
    val weatherForecast: WeatherForecastLayer
)

data class WeatherForecastLayer(
    @SerializedName("time")
    val weatherForecastTimeSlot: List<WeatherForecastTimeSlot>
)


data class WeatherForecastTimeSlot(
    @SerializedName("to")
    val toTime: String,

    @SerializedName("from")
    val fromTime: String,

    @SerializedName("location")
    val forecastInfo: LocationForecast

) : Comparable<WeatherForecastTimeSlot> {
    override operator fun compareTo(other: WeatherForecastTimeSlot): Int {
        if (this.toTime > other.toTime) return 1
        if (this.toTime < other.toTime) return -1
        return 0
    }
}

data class LocationForecast(
    @SerializedName("temperature")
    val temp: WeatherTemp,

    @SerializedName("symbol")
    val symbol: WeatherSymbol,

    @SerializedName("precipitation")
    val rain: WeatherRain
)

data class WeatherTemp(val unit: String, val value: String, val id: String)
data class WeatherSymbol(val number: String, val id: String)
data class WeatherRain(val unit: String, val value: String, val minValue: String, val maxValue: String)*/

// For versjon 2.0 av apiet

data class WeatherForecast(
    @SerializedName("properties")
    val weatherForecastTimeSlotList: WeatherForecastTimeSlotList

)

data class WeatherForecastTimeSlotList(
    @SerializedName("timeseries")
    val list: List<WeatherForecastTimeSlot>

)

data class WeatherForecastTimeSlot(
    @SerializedName("time")
    val time: String,

    @SerializedName("data")
    val types: WeatherForecastTypes

) : Comparable<WeatherForecastTimeSlot> {
    override operator fun compareTo(other: WeatherForecastTimeSlot): Int {
        if (this.time > other.time) return 1
        if (this.time < other.time) return -1
        return 0
    }
}

data class WeatherForecastTypes(
    @SerializedName("instant")
    val instantWeatherForecast: InstantWeatherForecast,

    @SerializedName("next_1_hour")
    val nextOneHourForecast: NextOneHourForecast
)

data class InstantWeatherForecast(
    @SerializedName("details")
    val details: WeatherForecastDetails
)
data class WeatherForecastDetails(
    @SerializedName("air_temperature")
    val temp: Float,

    @SerializedName("ultraviolet_index_clear_sky")
    val uv: Float,

    @SerializedName("precipitation_amount")
    val rainAmount: Float,

    @SerializedName("probability_of_thunder")
    val thunderProbability: Float
)

data class NextOneHourForecast(
    @SerializedName("summary")
    val summary: WeatherForecastSymbol,

    @SerializedName("details")
    val details: WeatherForecastDetails
)

data class WeatherForecastSymbol(val symbol: String)

