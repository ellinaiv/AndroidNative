package com.example.team11.database.dao

import com.example.team11.database.entity.WeatherForecastDb

package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.team11.database.entity.Place

@Dao
interface WeatherForecastDao {

    /**
     * Setter inn nye steder, men setter kun inn de stedene som ikke ligger der fra før, regner med at steder ikke endrer posisjon, og det er for at favorite-informasjonen skal bevares
     * @param place en liste med alle stedene
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWeatherForecast(place: List<WeatherForecastDb>)

    /**
     * Legger til et sted som favoritt
     * @param placeId iden til stedet
     */
    @Query("UPDATE place  SET favorite = 1 WHERE id = :placeId")
    fun addFavorite(placeId: Int)

    /**
     * Fjerner et sted som favoritt
     * @param placeId iden til stedet
     */
    @Query("UPDATE place SET favorite = 0 WHERE id = :placeId")
    fun removeFavorite(placeId: Int)

    /**
     * Sjekker om et sted er favoritt
     * @param placeId iden til stedet
     * @return Livedata<Boolean>
     */
    @Query("SELECT favorite FROM place WHERE id  = :placeId")
    fun isPlaceFavorite(placeId: Int): LiveData<Boolean>

    /**
     * Henter ut alle steder
     * @return liste med alle steder
     */
    @Query("SELECT * FROM place")
    fun getPlaceList(): LiveData<List<Place>>

    /**
     * Henter ut liste med alle steder som er favoritter
     * @return liste med steder som er favoritter
     */
    @Query("SELECT * FROM place WHERE favorite = 1")
    fun getFavoritePlaceList(): LiveData<List<Place>>

}