package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team11.database.entity.PersonalPreference

@Dao
interface PersonalPreferenceDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalPreference(personalPreference: PersonalPreference)

    /**
     * Henter ut tid for når en database sist ble cachet
     * @param tableName_, navnet på den databasen man vil sjekke, hent navnet fra dbConstants-filen
     */
    @Query("SELECT * FROM personal_preference")
    fun getPersonalPreference(): LiveData<List<PersonalPreference>>
}