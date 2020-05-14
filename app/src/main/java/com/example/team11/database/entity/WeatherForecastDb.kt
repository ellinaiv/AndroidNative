package com.example.team11.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.team11.Place
import com.example.team11.util.DbConstants


@Entity(tableName = DbConstants.WEATHER_FORECAST_TABLE_NAME,
    foreignKeys = [ForeignKey(entity = Place::class, parentColumns = arrayOf("id"), childColumns = arrayOf("placeId"), onDelete = ForeignKey.CASCADE)])
data class WeatherForecastDb(
    @ColumnInfo(name = "place_id")
    @PrimaryKey val placeId: Int,
    @ColumnInfo(name = "hour_forecast")
    var hourForecast: HourForecast,
    @ColumnInfo(name = "day_forecast")
    val dayForecast: DayForecast
) {

    open class WeatherForecast(
        var time: String,
        var symbol: String,
        @ColumnInfo(name = "temp_air")
        var tempAir: Int,
        var precipitation: Float,
        var uv: Float)

    class HourForecast(
        time: String,
        symbol: String,
        tempAir: Int,
        precipitation: Float,
        uv: Float
    ) : WeatherForecast( time, symbol, tempAir, precipitation, uv)

    class DayForecast(
        time: String,
        symbol: String,
        tempAir: Int,
        precipitation: Float,
        uv: Float
    ) : WeatherForecast( time, symbol, tempAir, precipitation, uv)
}