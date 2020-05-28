package com.example.team11.api.valueObjects

import com.google.gson.annotations.SerializedName


// For versjon 2.0 av apiet

/**
 * Brukes for å legge in data fra API
 */

data class WeatherForecastApi(
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

){
    override fun toString(): String {
        return "$time\n"
    }
}


data class WeatherForecastTypes(
    @SerializedName("instant")
    val instantWeatherForecast: InstantWeatherForecast,

    @SerializedName("next_1_hours")
    val nextOneHourForecast: NextHoursForecast?,

    @SerializedName("next_6_hours")
    val nextSixHourForecast: NextHoursForecast?
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

data class NextHoursForecast(
    @SerializedName("summary")
    val summary: WeatherForecastSymbol,

    @SerializedName("details")
    val details: WeatherForecastDetails
)

data class WeatherForecastSymbol(
    @SerializedName("symbol_code")
    val symbol: String)

