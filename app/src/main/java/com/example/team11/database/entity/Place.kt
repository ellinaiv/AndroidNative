package com.example.team11.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.team11.util.DbConstants
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import kotlin.random.Random

@Entity(tableName = DbConstants.PLACE_TABLE_NAME)
class Place(
    @PrimaryKey val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    var favorite: Boolean? = false,
    var tempWater: Int = Random.nextInt(0, 35),
    var tempAir: Int = Random.nextInt(-30, 35)
): Serializable {

    /**
     * Returerer posisjon i et latlng objekt
     * @return latlng med posisjonen
     */

    fun getLatLng():LatLng = LatLng(lat, lng)

    /**
     * Sammenligner en strand med en annen
     * @param other Any[Strand], kan ikke være null
     * @return en Int. 0 > hvis this er større en other, 0 hvis this == strand
     * og 0 < hvis this er mindre enn other
     */
    /*
    override fun compareTo(other: Place): Int {
        TODO("Slette denne metoden? ")
    }*/

    override fun toString(): String {
        return "$id:$name[$lat,$lng]"
    }
}
