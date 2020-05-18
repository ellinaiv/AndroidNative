package com.example.team11.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.team11.util.DbConstants
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import kotlin.random.Random

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
        return "$id:$name[$lat,$lng]"
    }
}
