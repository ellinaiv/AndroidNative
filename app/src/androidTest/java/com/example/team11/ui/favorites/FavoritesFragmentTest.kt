package com.example.team11.ui.favorites

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.example.team11.R
import com.example.team11.ui.bottomNavigation.MainActivity
import com.example.team11.ui.placesList.PlacesListFragment
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep


class FavoritesFragmentTest{

    /**
     * For at denne testen skal kjøres riktig må man slette appen.
     * Det kommer av at hvis brukern har lagt til noen  favoritter vil
     * dette pårvirke resultatet av testene (Grunnet at det blir lagret
     * i databasen). Vi kunne ha hentet ut
     * viewmodel og hentet ut lista med favoriter, men da
     * måtte det gå på bekostning av at viewModelen måtte være public
     * i FavoritesFragments noe vi ikke ønsker.
     */

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup(){
        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.recycler_viewPlaces))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.toggleFavorite))
            .perform(click())
        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)

        sleep(1000)

        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.recycler_viewPlaces))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        onView(withId(R.id.toggleFavorite))
            .perform(click())
        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)


        sleep(1000)

        launchFragmentInContainer<PlacesListFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.recycler_viewPlaces))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(6, click()))
        onView(withId(R.id.toggleFavorite))
            .perform(click())
        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)

        sleep(1000)

    }
    @Test
    fun testFavouriteMarked(){

        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
        sleep(1000)
        onView(withId(R.id.recycler_view))
            .check( RecyclerViewItemCountAssertion(3))
    }

    @Test
    fun testFavouriteUnmarked(){

        launchFragmentInContainer<FavoritesFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.recycler_view))
            .check( RecyclerViewItemCountAssertion(0))
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