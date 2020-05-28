package com.example.team11.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team11.database.entity.MetadataTable

@Dao
interface MetadataDao {

    /**
     * Setter inn info om når en database sist ble cachet, har bare en entry per database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateDateLastCached(metadata: MetadataTable)

    /**
     * Henter ut tid for når en database sist ble cachet
     * @param tableName_, navnet på den databasen man vil sjekke, hent navnet fra dbConstants-filen
     */
    @Query("SELECT date_last_cached FROM metadata WHERE table_name = :tableName_")
    fun getDateLastCached(tableName_: String): Long
}