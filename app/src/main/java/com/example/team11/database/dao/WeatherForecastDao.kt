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
     * Legger til currentSeaSpeed
     * @param [placeId] id på stedet [speed] current sea speed for stedet
     */
    @Query("UPDATE weather_forecast SET speed = :speed WHERE place_id = :placeId")
    fun addSpeed(placeId: Int, speed: Double)

    /**
     * Sletter forecast for et sted, dette skjer hver gang nye forecasts for et sted legges inn
     * @param placeId Id for stedet
     */
    @Query("DELETE FROM weather_forecast WHERE place_id = :placeId")
    fun deleteForecastsForPlace(placeId: Int)

    /**
     * Sjekker om det finnes forecast for det gitte stedet
     */
    @Query("SELECT COUNT(*) FROM weather_forecast WHERE place_id = :placeId")
    fun forecastsExist(placeId: Int): Boolean

    /**
     * Henter ut alle forecast for alle steder med tidspunkt nå
     * @param placeId Id for alle stedene
     * @return liste med HourForecast
     */
    @Query("SELECT * FROM weather_forecast WHERE time IN (:wantedTimes) AND place_id in (:placeIds) ORDER BY place_id")
    fun getTimeForecastsList(placeIds: List<Int>, wantedTimes: List<String>): LiveData<List<WeatherForecastDb>>

    /**
     * Henter ut alle forecast for alle steder med tidspunkt nå
     * @param placeId Id for stedet
     * @return liste med HourForecast
     */
    @Query("SELECT * FROM weather_forecast WHERE place_id = :placeId AND time IN (:wantedTimes) ORDER BY place_id, forecast_id")
    fun getTimeForecast(placeId: Int, wantedTimes: List<String>): LiveData<List<WeatherForecastDb>>

    /**
     * Henter ut antall forecasts i databasen, for å sjekke om databasen er tom
     */
    @Query("SELECT COUNT(*) FROM weather_forecast")
    fun getNumbForecast(): Int

}