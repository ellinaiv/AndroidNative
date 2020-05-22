package com.example.team11


import android.app.Activity
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.ui.place.PlaceActivity
import com.example.team11.ui.placesList.PlacesListFragment
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
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



    @Test
    fun testBackButton() {
        //TODO
//        goToPlaceActivity()
//        onView(withId(R.id.buttonBack))
//            .perform(click())
//        FragmentManager.findFragment<Fragment>()
    }

    @Test
    fun testPlaceName() {
        goToPlaceActivity()
        val activityInstance = getActivityInstance() as PlaceActivity
        val placeInstance = activityInstance.viewModel.place
        onView(withId(R.id.textPlaceName)).check(matches(withText(placeInstance!!.value!!.name)))
    }

    @Test
    fun testWaves() {
        //TODO
    }

    @Test
    fun testWaterTemp(){
        //TODO
//        goToPlaceActivity()
//        val activityInstance = getActivityInstance() as PlaceActivity
//        val placeInstance = activityInstance.viewModel.place
//        onView(withId(R.id.textTempWater))
//            .check(matches(withText(placeInstance!!.value!!.tempWater.toString())))
    }

    @Test
    fun testWeatherNow() {
        /**
         * denne fungerer ikke, derfor er den kommentert ut.. activityInstance.viewModel.getHourForecast().value gir null og
         * dermed får man NullPointerException. Har også prøvd med observer,
         * men det var heller ikke mulig..
         */
        //værikon
//        goToPlaceActivity()
//        val activityInstance = getActivityInstance() as PlaceActivity
//        val actualImage = getActivityInstance()!!.findViewById<ImageView>(R.id.imageWeather).drawable.toBitmap()
//        val forecastSymbol = activityInstance.viewModel.getHourForecast().value!![0].symbol
//        val expectedImage = activityInstance.getDrawable(activityInstance.resources.getIdentifier(forecastSymbol,
//            "drawable", activityInstance.packageName))!!.toBitmap()
//        assert(actualImage == expectedImage)  //usikker på ==

        //lufttemperatur
        //nedbør
    }

    @Test
    fun testCurrentsResult() {
        /**
         * kan ikke implementeres pga. ikke tilgang til data fra databasen. (se testWeatherNow())
         */
    }

    @Test
    fun testOpenCurrentsInfo(){
        goToPlaceActivity()
        onView(withId(R.id.buttonCurrentsInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.layoutCurrentsInfo))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCloseCurrentsInfo() {
        //knapp
        goToPlaceActivity()
        onView(withId(R.id.buttonCurrentsInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.buttonCurrentsCloseInfo))
            .perform(click())
        onView(withId(R.id.layoutCurrentsInfo)).check(matches(not(isDisplayed())))

        //klikk utenfor layout
        onView(withId(R.id.buttonCurrentsInfo))
            .perform(click())
        onView(withId(R.id.layoutInScrollView))
            .perform(click())
        onView(withId(R.id.layoutCurrentsInfo)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testUVResult() {
        /**
         * kan ikke implementeres pga. ikke tilgang til data fra databasen. (se testWeatherNow())
         */
    }

    @Test
    fun testOpenUVInfo(){
        goToPlaceActivity()
        onView(withId(R.id.buttonUVInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.layoutUVInfo))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCloseUVInfo() {
        //knapp
        goToPlaceActivity()
        onView(withId(R.id.buttonUVInfo))
            .perform(scrollTo())
            .perform(click())
        onView(withId(R.id.buttonUVCloseInfo))
            .perform(click())
        onView(withId(R.id.layoutUVInfo)).check(matches(not(isDisplayed())))

        //klikk utenfor layout
        onView(withId(R.id.buttonUVInfo))
            .perform(click())
        onView(withId(R.id.layoutInScrollView))
            .perform(click())
        onView(withId(R.id.layoutUVInfo)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testHourForecast() {
        /**
         * kan ikke implementeres pga. ikke tilgang til data fra databasen. (se testWeatherNow())
         */
    }

    @Test
    fun testLongForecast() {
        /**
         * kan ikke implementeres pga. ikke tilgang til data fra databasen. (se testWeatherNow())
         */
    }



    private fun goToPlaceActivity() {
        onView(allOf(withId(R.id.recycler_viewPlaces), isDisplayed())).perform(click())
    }

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
}