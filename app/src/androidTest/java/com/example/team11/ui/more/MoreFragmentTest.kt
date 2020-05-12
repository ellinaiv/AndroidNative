package com.example.team11.ui.more

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MoreFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        launchFragmentInContainer<MoreFragment>(themeResId = R.style.AppTheme)
    }

    @Test
    fun testAppTitleClickOpen(){
        onView(withId(R.id.textAboutAppTitle))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.textAboutApp)).check(matches(isDisplayed()))
        onView(withId(R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAppTitleClickClose(){
        testAppTitleClickOpen()
        onView(withId(R.id.textAboutAppTitle))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAPITitleClickOpen(){
        onView(withId(R.id.textAboutAPITitle))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.textAboutAPI)).check(matches(isDisplayed()))
        onView(withId(R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAPITitleClickClose(){
        testAPITitleClickOpen()
        onView(withId(R.id.textAboutAPITitle))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.textAboutApp)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.textAboutAPI)).check(matches(ViewMatchers
            .withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun testAppThenAPI(){
        testAppTitleClickOpen()
        testAPITitleClickOpen()
    }

    @Test
    fun testAPIThenApp(){
        testAPITitleClickOpen()
        testAppTitleClickOpen()
    }
}