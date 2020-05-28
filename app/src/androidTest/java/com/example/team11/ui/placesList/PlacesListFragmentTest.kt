package com.example.team11.ui.placesList


import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Test
import com.example.team11.R
import com.example.team11.ui.bottomNavigation.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlacesListFragmentTes{

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
    }

    @Test
    fun testPlacesList(){

        onView(withId(R.id.searchText))
            .perform(typeText("Bekkensten"))
        onView(withId(R.id.textName)).check(matches(withText("Bekkensten")))
    }
    @Test
    fun testFragmentRecreation() {
        val scenario = launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
        scenario.recreate()
    }

    @Test
    fun testScrolling(){
        onView(withId(R.id.recycler_viewPlaces)).perform(ViewActions.swipeUp())
        onView(withId(R.id.recycler_viewPlaces)).perform(ViewActions.swipeDown())
    }

    @Test
    fun testClickable(){
        onView(allOf(withId(R.id.recycler_viewPlaces), isDisplayed()))
            .perform(click())

    }

}