package com.example.team11.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(tableName = "weather_forecast",
    foreignKeys = [ForeignKey(entity = Place::class, parentColumns = arrayOf("id"), childColumns = arrayOf("place_id"), onDelete = CASCADE)],
    primaryKeys = ["place_id","time"])
data class WeatherForecastDb(
    @ColumnInfo(name = "place_id")
    val placeId: Int,
    @ColumnInfo(name = "forecast_id")
    val forecastId: Int,
    var time: String,
    var symbol: String,
    @ColumnInfo(name = "temp_air")
    var tempAir: Int,
    var precipitation: Float,
    var uv: Float,
    var speed: Double
)