package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.team11.database.entity.Place

@Dao
interface PlaceDao {

    /**
     * Setter inn nye steder, men setter kun inn de stedene som ikke ligger der fra før,
     * regner med at steder ikke endrer posisjon, og det er for at favorite-informasjonen
     * skal bevares
     * @param place en liste med alle stedene
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaceList(place: List<Place>)

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
     * Sjekker om et sted er favoritt
     * @param placeId iden til stedet
     * @return Livedata<Boolean>
     */
    @Query("SELECT favorite FROM place WHERE id  = :placeId")
    fun isPlaceFavoriteNonLivedata(placeId: Int): LiveData<Boolean>

    /**
     * Henter ut alle steder
     * @return liste med alle steder
     */
    @Query("""
        SELECT * 
        FROM place 
        WHERE (place.tempWater < (
            SElECT pp.waterTempMid 
            FROM personal_preference as pp
        ) 
        AND (
            SELECT pp.showWaterCold 
            FROM personal_preference as pp
        ) 
        OR place.tempWater > (
            SElECT pp.waterTempMid 
            FROM personal_preference as pp
        ) AND  (
            SELECT pp.showWaterWarm
            FROM personal_preference as pp
        )) AND (((SELECT COUNT(*) FROM weather_forecast as wf WHERE wf.place_id = place.id AND wf.time = :timeNow) = 0)
         OR ((
            SELECT wf.temp_air 
            FROM weather_forecast as wf 
            WHERE place_id = place.id
            AND wf.time = :timeNow
            LIMIT 1
        ) < (
            SElECT pp.airTempMid 
            FROM personal_preference as pp
        ) AND (
            SELECT pp.showAirCold
            FROM personal_preference as pp
        ) OR ((
            SELECT wf.temp_air 
            FROM weather_forecast as wf 
            WHERE wf.place_id = place.id
            AND wf.time = :timeNow
            LIMIT 1
        ) > (
            SElECT pp.airTempMid 
            FROM personal_preference as pp
        ) AND (
            SELECT pp.showAirWarm
            FROM personal_preference as pp
        ))))
            ORDER BY id
        """)
    fun getPlaceList(timeNow: String): LiveData<List<Place>>

    @Query("SELECT COUNT(*) FROM place")
    fun getNumbPlaces(): Int


    /**
     * Henter ut liste med alle steder som er favoritter
     * @return liste med steder som er favoritter
     */
    @Query("SELECT * FROM place WHERE favorite = 1 ORDER BY id")
    fun getFavoritePlaceList(): LiveData<List<Place>>

}