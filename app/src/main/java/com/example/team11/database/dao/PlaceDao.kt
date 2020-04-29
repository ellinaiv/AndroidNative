package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team11.database.entity.Place

interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaceList(place: List<Place>)

    @Query("SELECT * FROM place WHERE id = :id_")
    fun getPlace(id_: String): Place

    @Query("SELECT * FROM place")
    fun getPlaceList(): LiveData<List<Place>>
}