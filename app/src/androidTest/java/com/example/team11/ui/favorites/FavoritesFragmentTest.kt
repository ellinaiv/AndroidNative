package com.example.team11.ui.favorites

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.example.team11.R
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.ui.placesList.PlacesListFragment
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FavoritesFragmentTest{

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp(){
        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.toggleFavourite))
         //   .perform(click())
    }

    @Test
    fun testFavouriteMarked(){

//        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
  //      onView(withId(R.id.recycler_viewPlaces))
  //              .perform(ViewActions.swipeUp())
   //     onView(allOf(withId(R.id.recycler_viewPlaces), isDisplayed()))
    //        .perform(click())
     //   onView(withId(R.id.toggleFavourite))
      //      .perform(click())

        //launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
       // onView(withId(R.id.recycler_view))
        //    .check( RecyclerViewItemCountAssertion(2))
    }

    @Test
    fun testFavouriteUnmarked(){
        //launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
        //onView(withId(R.id.recycler_view))
        //    .perform(click())
        //onView(withId(R.id.toggleFavourite))
          //  .perform(click())
        //launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
     //   onView(withId(R.id.recycler_view))
       //     .check( RecyclerViewItemCountAssertion(1))
    }
}

class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter!!.itemCount, `is`(expectedCount))
    }

}