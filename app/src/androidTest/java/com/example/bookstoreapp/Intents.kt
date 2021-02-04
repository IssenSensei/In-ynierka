package com.example.bookstoreapp

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookstoreapp.books.BookDetailFragment
import com.example.bookstoreapp.books.BooksRecyclerViewAdapter
import com.example.bookstoreapp.matcher.RecyclerViewWithIdAction
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Testy sterowania przejść między oknami
 */

@RunWith(AndroidJUnit4::class)
class ActivityStackTraceInstrumentedTest {


    private var bottomNavigation: BottomNavigationView? = null


    @Rule
    @JvmField
    val mainActivityRule = IntentsTestRule(MainActivity::class.java)

    @Before
    fun setUpBefore() {
        bottomNavigation = mainActivityRule.activity.findViewById(R.id.navigation_graph)
    }

    @After
    fun setUpAfter() {
        mainActivityRule.finishActivity()
    }


    @Test
    fun goToBookListAndThenDetails() {
        Thread.sleep(3000L)

        onView(
            allOf(
                withText(mainActivityRule.activity.resources.getString(R.string.navigation_books)),
                isDescendantOfA(withId(R.id.navigation_graph)),
                isDisplayed()
            )
        )
            .perform(click())
        Thread.sleep(3000L)

        assertTrue(
            bottomNavigation!!.menu.findItem(R.id.navigation_books).isChecked)

        mainActivityRule.activity

        onView(withId(R.id.books_recycler_view)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<BooksRecyclerViewAdapter.ViewHolder>(
                0,
                RecyclerViewWithIdAction.clickChildViewWithId(R.id.lyt_parent)
            )
        )
        Thread.sleep(3000L)

        Log.d("Current activity", mainActivityRule.activity.toString())
        Thread.sleep(3000L)


        intended(hasComponent(BookDetailFragment::class.java.name))
    }
}


