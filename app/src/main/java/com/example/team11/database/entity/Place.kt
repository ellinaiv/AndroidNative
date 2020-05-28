package com.example.team11.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Klasse med entity for place_databasen, lagrer all informasjon om steder + vanntemperatur
 */

@Entity(tableName = "place")
class Place(
    @PrimaryKey val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    var tempWater: Int,
    var favorite: Boolean? = false
) {

    override fun toString(): String {
        return "$id:$name[$lat,$lng] tempwater: $tempWater\n"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (id != other.id) return false
        if (name != other.name) return false
        if (lat != other.lat) return false
        if (lng != other.lng) return false
        if (tempWater != other.tempWater) return false
        if (favorite != other.favorite) return false

        return true
    }


}
