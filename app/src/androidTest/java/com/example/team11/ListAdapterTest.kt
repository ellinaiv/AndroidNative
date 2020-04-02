package com.example.team11

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Assert.*
import org.junit.Rule
import androidx.test.rule.ActivityTestRule
import org.junit.Test

class ListAdapterTest(){

    @Rule
    @JvmField
    val rule : ActivityTestRule<PlacesListActivity> = ActivityTestRule(PlacesListActivity::class.java)

    @Test
    fun testCardViewValues(){
        //onView(withId(R.id.tempAir)).check(matches(withText("no data")))
    }


}