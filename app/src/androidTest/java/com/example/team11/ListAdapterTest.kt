package com.example.team11

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.team11.deprecated.PlacesListActivity
import org.junit.Rule
import org.junit.Test

class ListAdapterTest(){

    @get:Rule
    val intentsTestRule = IntentsTestRule(PlacesListActivity::class.java)

    private val resultData = Intent()
    private val place1 = Place(0, "sørenga", 1.0,2.3)
    private val places : ArrayList<Place> = ArrayList<Place>(1)

    @Test
    fun testCardViewValues(){

        places.add(place1)
        resultData.putExtra("PLACES_LIST", places)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(toPackage("com.example.team11")).respondWith(result)
        onView(withId(R.id.textName)).check(matches(withText(places[0].name)))

    }


}