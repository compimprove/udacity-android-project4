package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    private lateinit var localDataSource: FakeDataSource
    private lateinit var appContext: Application

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        localDataSource = FakeDataSource(mutableListOf())
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { localDataSource as ReminderDataSource }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun clickAddReminderButton_navigateToAddEditFragment() {
        // GIVEN - On the ReminderList screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the "+" button
        onView(withId(R.id.addReminderFAB)).perform(click())
        // THEN - Verify that we navigate to the add screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun snackBar_showError() {
        // GIVEN - On the ReminderList screen
        localDataSource.setResponseError("Something when wrong")
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        // THEN - Verify that snackbar will show
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Something when wrong")))
    }

    @Test
    fun snackBar_showListData() {
        // GIVEN - On the ReminderList screen
        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")
        val reminder2 = ReminderDTO("Title2", "Description1", "Location1", 1.0, 2.0, "id2")
        val reminder3 = ReminderDTO("Title3", "Description1", "Location1", 1.0, 2.0, "id3")
        localDataSource.setData(mutableListOf(reminder1, reminder2, reminder3))

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        // THEN - Verify that data will show enough
        var itemCount = 0
        scenario.onFragment {
            itemCount = it.activity!!.findViewById<RecyclerView>(R.id.remindersRecyclerView)!!.childCount
        }

        MatcherAssert.assertThat(
            itemCount,
            CoreMatchers.equalTo(3)
        )
    }
}