package com.example.team11.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.team11.Place
import com.example.team11.util.DbConstants


@Entity(tableName = DbConstants.FORECAST_TABLE_NAME,
    foreignKeys = [ForeignKey(entity = Place::class, parentColumns = arrayOf("id"), childColumns = arrayOf("placeId"), onDelete = ForeignKey.CASCADE)])
data class WeatherForecastDb(
    @PrimaryKey val placeId: Int,
    val time: String,
    val symbol: String,
    val tempAir: Int,
    val precipitation: Float
)