package com.example.team11.database.dao

import com.example.team11.database.entity.WeatherForecastDb
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherForecastDao {

    /**
     * Setter inn nye forecats
     * @param forecast, en liste med alle WeatherForecastDb
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherForecast(forecast: List<WeatherForecastDb>)

    /**
     * Henter ut alle forecast for de neste timene
     * @return liste med HourForecast
     */
    @Query("SELECT * FROM weather_forecast WHERE place_id = :placeId")
    fun getHourForecast(placeId: Int): LiveData<List<WeatherForecastDb.HourForecast>>

    /**
     * Henter ut liste med alle steder som er favoritter
     * @return liste med steder som er favoritter
     */
    @Query("SELECT * FROM place WHERE favorite = 1")
    fun getDayForecast(): LiveData<List<WeatherForecastDb.DayForecast>>

}