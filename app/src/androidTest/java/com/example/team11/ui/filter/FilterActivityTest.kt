package com.example.team11.ui.filter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.team11.database.entity.PersonalPreference
import com.example.team11.R
import com.example.team11.util.Constants
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class FilterActivityTest{


    @get :Rule
    val activityTestRule = ActivityTestRule(FilterActivity::class.java)
    private lateinit var appContext: Context

    @Before
    fun setup(){
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.team11", appContext.packageName)
    }

    @Test
    fun testSeekBarWaterTextMid(){
        val temp = Constants.waterTempHigh - 1
        onView(withId(R.id.seekBarWater))
            .perform(scrollTo())
            .perform(setProgress(temp))
        onView(withId(R.id.textTempMidWater))
            .check(matches(withText(appContext.getString(R.string.tempC, temp))))
    }

    @Test
    fun testSeekBarAirTextMid(){
        val temp = Constants.airTempHigh - 1
        onView(withId(R.id.seekBarAir))
            .perform(scrollTo())
            .perform((setProgress(temp + abs(Constants.airTempLow))))
        onView(withId(R.id.textTempMidAir))
            .check(matches(withText(appContext.getString(R.string.tempC, temp))))
    }

    @Test
    fun testTextHighAirAndTextLowAir(){
        onView(withId(R.id.textTempHighAir))
            .check(matches(withText(appContext.getString(R.string.tempC, Constants.airTempHigh))))
        onView(withId(R.id.textTempLowAir))
           .check(matches(withText(appContext.getString(R.string.tempC, Constants.airTempLow))))
    }

    @Test
    fun testTextHighWaterAndTextLowWater(){
        onView(withId(R.id.textTempHighWater))
           .check(matches(withText(appContext.getString(R.string.tempC, Constants.waterTempHigh))))
        onView(withId(R.id.textTempLowWater))
           .check(matches(withText(appContext.getString(R.string.tempC, Constants.waterTempLow))))
    }

    @Test
    fun testCheckBoxInit(){
        onView(withId(R.id.checkBoxColdAir))
            .check(matches(isChecked()))
        onView(withId(R.id.checkBoxWarmAir))
            .check(matches(isChecked()))
        onView(withId(R.id.checkBoxColdWater))
            .check(matches(isChecked()))
        onView(withId(R.id.checkBoxWarmWater))
            .check(matches(isChecked()))
    }

    //insperasjon til kode hentet fra: https://stackoverflow.com/questions/23659367/espresso-set-seekbar
    //skrevet om til kotlin
    //forfatter: Luigi Massa Gallerano
    private fun setProgress(progress: Int): ViewAction? {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as SeekBar
                seekBar.progress = progress
            }

            override fun getDescription(): String {
                return "Set a progress on a SeekBar"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(SeekBar::class.java)
            }
        }
    }

}