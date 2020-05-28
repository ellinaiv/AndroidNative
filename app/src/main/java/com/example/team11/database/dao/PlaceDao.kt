package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.team11.database.entity.Place

@Dao
interface PlaceDao {

    /**
     * Setter inn nye steder, og erstatter gammel informasjon med ny informasjon
     * @param place en liste med alle stedene
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
     * Sjekker om et sted er favoritt og returnerer non.livedata slik at dette kan brukes i
     * repository når nye info om steder skal legges inn må denne informasjonen videreføres
     * @param placeId iden til stedet
     * @return Boolean
     */
    @Query("SELECT favorite FROM place WHERE id  = :placeId")
    fun isPlaceFavoriteNonLiveData(placeId: Int): Boolean

    /**
     * Sjekker om et sted eksisterer
     * @param placeId
     * @return Boolean
     */
    @Query("SELECT COUNT(*) FROM place WHERE id = :placeId")
    fun placeExists(placeId: Int): Boolean

    /**
     * Henter ut alle ønskede steder basert på personal preference, så denne henter bare ut
     * steder i samsvar med filtrering
     * @return liste med alle steder som oppfyller filtreringskriterie
     */
    @Query("""
        SELECT * 
        FROM place 
        WHERE (place.tempWater < (
            SELECT pp.waterTempMid 
            FROM personal_preference as pp
        ) 
        AND (
            SELECT pp.showWaterCold 
            FROM personal_preference as pp
        ) 
        OR place.tempWater >= (
            SElECT pp.waterTempMid 
            FROM personal_preference as pp
        ) AND (
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
        ) >= (
            SElECT pp.airTempMid 
            FROM personal_preference as pp
        ) AND (
            SELECT pp.showAirWarm
            FROM personal_preference as pp
        ))))
            ORDER BY id
        """)
    fun getPlaceList(timeNow: String): LiveData<List<Place>>


    /**
     * Sjekker hvor mange steder som finnes i databasen for å sjekke om den er tom
     * @return Int
     */
    @Query("SELECT COUNT(*) FROM place")
    fun getNumbPlaces(): Int

    /**
     * Henter ut liste med alle steder som er favoritter
     * @return liste med steder som er favoritter
     */
    @Query("SELECT * FROM place WHERE favorite = 1 ORDER BY id")
    fun getFavoritePlaceList(): LiveData<List<Place>>

    /**
     * Endrer temperatur på badeplasser til en falsk temperatur
     * @param [noDataValue] Ny verdi på temperatur hvis badevannstemperaturer ikke finnes for et sted
     * [maxValue] maksverdi for badevannstemperatur
     * [minValue] minverdi for badetemperatur
     */
    @Query("UPDATE place SET tempWater = ABS(RANDOM()) % (:maxValue - :minValue) + :minValue WHERE tempWater = :noDataValue")
    fun changeToFalseData(noDataValue: Int, maxValue: Int, minValue: Int)


}