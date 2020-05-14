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

    interface WeatherForecast

    data class HourForecast(
        val time: String,
        val symbol: String,
        @ColumnInfo(name = "temp_air")
        val tempAir: Float,
        val precipitation: Float,
        val uv: Float
    ) : WeatherForecast

    data class DayForecast(
        val time: String,
        val symbol: String,
        @ColumnInfo(name = "temp_air")
        val tempAir: Float,
        val precipitation: Float,
        val uv: Float
    ) : WeatherForecast
}