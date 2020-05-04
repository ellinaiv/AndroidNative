package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.team11.database.entity.Place

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaceList(place: List<Place>)

    @Query("UPDATE place SET favorite = 1 WHERE id = :placeId")
    fun addFavorite(placeId: Int)

    @Query("SELECT * FROM place WHERE id = :placeId")
    fun getPlace(placeId: Int): Place

    @Query("SELECT * FROM place")
    fun getPlaceList(): LiveData<List<Place>>

    @Query("SELECT * FROM place WHERE favorite = 1")
    fun getFavoritePlaceList(): LiveData<List<Place>>
}