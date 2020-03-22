package com.example.team11
import com.google.android.gms.maps.model.LatLng

class Strand (navn: String, latLng: LatLng, tempratur: Int) : Comparable<Strand> {

    /**
     * tanken er at denne kan brukes i compareTo metoden
     * kan også være et enum, se diskusjon i PersonligPreferanse klassen
     */
    private var tempraturInnafor = true


    /**
     * Endrer tempraturInnafor slik at den er true hvis den er innafor, og false ellers
     *  @param nyPreferanse  Any [Int]
    */
    fun oppdaterTempraturInnafor(nyPreferanse: Int): Boolean{
        TODO("ikke implementert")
    }

    /**
     * Sammenligner en strand med en annen
     * @param other Any[Strand], kan ikke være null
     * @return en Int. 0 > hvis this er større en other, 0 hvis this == strand
     * og 0 < hvis this er mindre enn other
     */
    override fun compareTo(other: Strand): Int {
        TODO("ikke implemert")
    }

}