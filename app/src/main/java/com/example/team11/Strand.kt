package com.example.team11
import com.google.android.gms.maps.model.LatLng

class Strand (navn: String, latLng: LatLng, tempratur: Int) : Comparable<Strand> {

    var tempraturInnafor = true


    /**
     *  Sjekker om den nye tempraturen er innafor, og oppdaterer den
     *  @Tanker: Kommer ann på hvor mange grader vi vil ha av Innafor, vil vi ha
     *  f.eks. ikke snakk om/ kan gå / perfekt, da burde Innafor være et enum, og vi
     *  må ha en boolean sjekk for kan gå kan ikke være > en perfekt f.eks.
     *  @param nyPreferanse  Any [Int]
     *  @return en boolean. True hvis den ble oppdater, false hvis det ikke gitt
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