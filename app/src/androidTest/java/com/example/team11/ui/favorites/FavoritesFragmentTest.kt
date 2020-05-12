package com.example.team11.ui.favorites

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.team11.R
import com.example.team11.ui.bottomNavigation.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesFragmentTest{

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
    }

    @Test
    fun testFavourites(){

    }

}