package com.example.team11


import androidx.test.rule.ActivityTestRule
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.ui.place.PlaceActivity
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId


class PlaceActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(PlaceActivity::class.java)

    @Test
    fun testOpenCurrentsInfo() {
        onView(withId(R.id.buttonCurrentsInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.layoutCurrentsInfo))
            .check(matches(isDisplayed()))
    }
}