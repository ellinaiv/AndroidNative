package com.example.team11.database


import android.app.Person
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.team11.database.dao.MetadataDao
import com.example.team11.database.dao.PersonalPreferenceDao
import com.example.team11.database.dao.PlaceDao
import com.example.team11.database.entity.MetadataTable
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.database.entity.Place

/**
 * Room-database for denne appen, denne klassen oppretter databasen
 */
@Database(entities = [Place::class, MetadataTable::class, PersonalPreference::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun metadataDao(): MetadataDao
    abstract  fun personalPreferenceDao(): PersonalPreferenceDao


    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,
                "badeapp.db")
                .build()
        }
    }
}