package com.example.team11.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.team11.PersonalPreference
import com.example.team11.util.DbConstants
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import kotlin.random.Random

@Entity(tableName = DbConstants.PLACE_TABLE_NAME)
class Place(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    var temp: Int = Random.nextInt(0, 35),
    var favorite: Boolean): Serializable {

    /**
     * Returerer posisjon i et latlng objekt
     * @return latlng med posisjonen
     */

    fun getLatLng():LatLng = LatLng(lat, lng)

    /**
     * Sjekker om dette stedet er varmt
     * @return boolean
     */
    fun isWarm(): Boolean{
        return PersonalPreference.waterTempMid < temp
    }


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
