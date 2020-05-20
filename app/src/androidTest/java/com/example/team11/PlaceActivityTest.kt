package com.example.team11


import android.app.Activity
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.team11.database.entity.Place
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.ui.placesList.PlacesListFragment
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class PlaceActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
    }

    private val testPlace = Place(-1, "Badeplass", 0.1, 0.2, 16)

    private fun getActivityInstance(): Activity? {
        val currentActivity = arrayOf<Activity?>(null)
        getInstrumentation().runOnMainSync(Runnable {
            val resumedActivity =
                ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
            val it: Iterator<Activity> = resumedActivity.iterator()
            currentActivity[0] = it.next()
        })
        return currentActivity[0]
    }


    @Test
    fun testClickable(){
        onView(allOf(withId(R.id.recycler_viewPlaces), isDisplayed())).perform(click())
        val activityInstance = getActivityInstance() as PlaceActivity

        activityInstance.viewModel.place

        onView(withId(R.id.buttonCurrentsInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.layoutCurrentsInfo))
            .check(matches(isDisplayed()))
    }


//    @get:Rule
//    val activityTestRule = ActivityTestRule(PlaceActivity::class.java)
//
//    @Test
//    fun testOpenCurrentsInfo() {
//        onView(withId(R.id.buttonCurrentsInfo))
//            .perform(scrollTo())
//            .perform(click())
//        onView(withId(R.id.layoutCurrentsInfo))
//            .check(matches(isDisplayed()))
//    }
}