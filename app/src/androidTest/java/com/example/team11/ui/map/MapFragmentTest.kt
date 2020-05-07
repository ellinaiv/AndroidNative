package com.example.team11.ui.map

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.team11.R
import com.example.team11.ui.bottomNavigation.MainActivity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MapFragmentTest{

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)


    @Test
    fun testBottomMenuPresence(){
        val menu = activityTestRule.activity.bottomNavigationView
        assertNotNull(menu)
        assertEquals(5, menu.maxItemCount)
    }

    @Test
    fun testNavigationToList() {
        onView(withId(R.id.navigationList))
            .perform(ViewActions.click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testNavigationToFavourites() {
        onView(withId(R.id.navigationFavorites))
            .perform(ViewActions.click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testNavigationToMore() {
        onView(withId(R.id.navigationMore))
            .perform(ViewActions.click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testSearchView() {
        onView(withId(R.id.searchText))
            .perform(typeText("Bekke"))
            .check(matches(isDisplayed()))
    }
}