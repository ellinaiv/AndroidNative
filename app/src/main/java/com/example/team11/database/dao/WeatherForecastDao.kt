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
     * Henter ut alle forecast for alle steder med tidspunkt nå
     * @return liste med HourForecast
     */
    @Query("SELECT * FROM weather_forecast WHERE time IN (:wantedTimes) AND place_id in (:placeIds) ORDER BY place_id")
    fun getTimeForecastsList(placeIds: List<Int>, wantedTimes: List<String>): LiveData<List<WeatherForecastDb>>

    /**
     * Henter ut liste med alle steder som er favoritter
     * @return liste med steder som er favoritter
     */
    @Query("SELECT * FROM weather_forecast WHERE place_id = :placeId AND time IN (:wantedTimes)")
    fun getTimeForecast(placeId: Int, wantedTimes: List<String>): LiveData<List<WeatherForecastDb>>

    @Query("SELECT COUNT(*) FROM weather_forecast")
    fun getNumbForecast(): Int

}