package com.example.team11.ui.more

import androidx.appcompat.resources.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.team11.ui.bottomNavigation.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class MoreFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        launchFragmentInContainer<MoreFragment>(themeResId = com.example.team11.R.style.AppTheme)
    }

    @Test
    fun testAppTitleClickOpen(){
        onView(withId(com.example.team11.R.id.textAboutAppTitle)).perform(click())
        onView(withId(com.example.team11.R.id.textAboutApp)).check(matches(isDisplayed()))
        onView(withId(com.example.team11.R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAppTitleClickClose(){
        testAppTitleClickOpen()
        onView(withId(com.example.team11.R.id.textAboutAppTitle)).perform(click())
        onView(withId(com.example.team11.R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(com.example.team11.R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAPITitleClickOpen(){
        onView(withId(com.example.team11.R.id.textAboutAPITitle)).perform(click())
        onView(withId(com.example.team11.R.id.textAboutAPI)).check(matches(isDisplayed()))
        onView(withId(com.example.team11.R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAPITitleClickClose(){
        testAPITitleClickOpen()
        onView(withId(com.example.team11.R.id.textAboutAPITitle)).perform(click())
        onView(withId(com.example.team11.R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(com.example.team11.R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
}