package com.example.team11
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import kotlin.random.Random


class Place(val id: Int, val name: String, val lat: Double, val lng: Double,
            var favorite: Boolean = false, var temp: Int = Random.nextInt(0, 35)): Serializable {

    //TODO("Slette det under")
    /**
     * tanken er at denne kan brukes i compareTo metoden
     * kan også være et enum, se diskusjon i PersonligPreferanse klassen
     */
    private var tempraturInnafor = true


    //TODO("Slette det under")
    /**
     * Endrer tempraturInnafor slik at den er true hvis den er innafor, og false ellers
     *  @param nyPreferanse
    */
    fun oppdaterTempraturInnafor(nyPreferanse: Int): Boolean{
        TODO("ikke implementert")
    }


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
        TODO("ikke implemert")
    }*/





    override fun toString(): String {
        return "$id:$name[$lat,$lng]"
    }

}
