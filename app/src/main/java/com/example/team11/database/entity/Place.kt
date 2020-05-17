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
    var favorite: Boolean? = false,
    var tempWater: Int = Random.nextInt(0, 35),
    var tempAir: Int = Random.nextInt(-30, 35)
) {

    override fun toString(): String {
        return "$id:$name[$lat,$lng]"
    }
}
