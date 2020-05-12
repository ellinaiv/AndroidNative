package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.team11.database.entity.Place

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaceList(place: List<Place>)

    @Query("UPDATE place  SET favorite = 1 WHERE id = :placeId")
    fun addFavorite(placeId: Int)

    @Query("UPDATE place SET favorite = 0 WHERE id = :placeId")
    fun removeFavorite(placeId: Int)

    @Query("SELECT favorite FROM place WHERE id  = :placeId")
    fun isPlaceFavorite(placeId: Int): LiveData<Boolean>

    @Query("SELECT * FROM place WHERE id = :placeId")
    fun getPlace(placeId: Int): Place

    @Query("SELECT * FROM place")
    fun getPlaceList(): LiveData<List<Place>>

    @Query("SELECT * FROM place WHERE favorite = 1")
    fun getFavoritePlaceList(): LiveData<List<Place>>

}