package com.example.team11.ui.filter

import android.content.Context
import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.team11.PersonalPreference
import com.example.team11.R
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FilterActivityTest{


    @get :Rule
    val activityTestRule = ActivityTestRule(FilterActivity::class.java)
    lateinit var appContext: Context

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
        val temp = 22
        onView(withId(R.id.seekBarWater)).perform(setProgress(temp))
        onView(withId(R.id.textTempMidWater)).check(matches(withText(appContext.getString(R.string.tempC, temp))))
    }

    @Test
    fun testSeekBarAirTextMid(){
        val temp = 12
        val pp = PersonalPreference()
        onView(withId(R.id.seekBarAir)).perform((setProgress(temp - pp.airTempLow)))
        onView(withId(R.id.textTempMidAir)).check(matches(withText(appContext.getString(R.string.tempC, temp))))
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
                return ViewMatchers.isAssignableFrom(SeekBar::class.java)
            }
        }
    }

}