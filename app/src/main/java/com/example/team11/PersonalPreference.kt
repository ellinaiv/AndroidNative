package com.example.team11

import android.os.Bundle

class PersonalPreference {

    //satt den til MIN_VALUE nå, slik at alle strende automatisk kommer
    private var tempraturMinVerdi = Int.MIN_VALUE


    /**
     *  Sjekker om den nye tempraturen er innafor, og oppdaterer den
     *  @Tanker: Kommer ann på hvor mange grader vi vil ha av Innafor, vil vi ha
     *  f.eks. ikke snakk om/ kan gå / perfekt, da burde Innafor i klassen
     *  strand vvære et enum, og vi
     *  må ha en boolean sjekk for kan gå kan ikke være > en perfekt f.eks.
     *  @param nyPreferanse  Any [Int]
     *  @return en Boolean, true hvis den nye verdien er innfor og forandret, og
     *  false hvis den ikke er det.
     */
    fun setPreferenceWaterTemp(good: Int, medium: Int, bad: Int): Boolean{
        TODO("Ikke implemntert")
    }
}



enum class Preference{
    GOOD, MEDIUM, BAD
}