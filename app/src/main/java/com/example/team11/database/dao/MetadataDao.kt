package com.example.team11.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team11.database.entity.MetadataTable

@Dao
interface MetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateDateLastCached(metadata: MetadataTable)

    @Query("SELECT date_last_cached FROM metadata WHERE table_name = :tableName_")
    fun getDateLastCached(tableName_: String): Long?
}