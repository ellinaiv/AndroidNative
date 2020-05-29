package com.example.team11.uiAndViewModels.bottomNavigation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.team11.R
import junit.framework.TestCase.assertEquals
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BottomNavigationTest{

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
            .perform(click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testNavigationToFavourites() {
        onView(withId(R.id.navigationFavorites))
            .perform(click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testNavigationToMore() {
        onView(withId(R.id.navigationMore))
            .perform(click())
            .check(matches(isDisplayed()))
    }
    @Test
    fun testSearchView() {
        onView(withId(R.id.searchText))
            .perform(typeText("Bekkensten"))
            .check(matches(isDisplayed()))
    }
}