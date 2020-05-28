package com.example.team11.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.team11.database.entity.PersonalPreference

@Dao
interface PersonalPreferenceDao{

    /**
     * Setter in nye data for personal preference, erstatter gammel data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPersonalPreference(personalPreference: PersonalPreference)

    /**
     * Henter ut tid for når en database sist ble cachet
     * @param tableName_, navnet på den databasen man vil sjekke, hent navnet fra dbConstants-filen
     */
    @Query("SELECT * FROM personal_preference")
    fun getPersonalPreference(): LiveData<List<PersonalPreference>>

    /**
     * Henter ut om bruker ønsker å vise falsk data
     */
    @Query("SELECT falseData FROM personal_preference LIMIT 1")
    fun getFalseData(): Boolean

    /**
     * Setter falsk data til boolean
     * @param boolean
     */
    @Query("UPDATE personal_preference SET falseData = :boolean")
    fun changeFalseData(boolean: Boolean)
}