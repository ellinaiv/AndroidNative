package com.example.team11.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
class MetadataTable(
    @PrimaryKey @ColumnInfo(name = "table_name") val tableName: String,
    @ColumnInfo(name = "date_last_cached") val dateLastCached: Long
)