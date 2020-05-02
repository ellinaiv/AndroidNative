package com.example.team11.util

import androidx.room.TypeConverter
import java.util.*


//Kode fra https://developer.android.com/training/data-storage/room/referencing-data
// TODO("Hvordan konvertere")

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return if (date == null) {
            null
        } else {
            date.getTime()
        }
    }
}