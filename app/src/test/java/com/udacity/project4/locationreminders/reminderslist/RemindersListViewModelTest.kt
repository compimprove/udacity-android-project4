package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var dataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")
        val reminder2 = ReminderDTO("Title2", "Description1", "Location1", 1.0, 2.0, "id2")
        val reminder3 = ReminderDTO("Title3", "Description1", "Location1", 1.0, 2.0, "id3")
        dataSource = FakeDataSource(mutableListOf(reminder1, reminder2, reminder3))
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @Test
    fun loadReminders_eventLoading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.equalTo(true)
        )
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.equalTo(false)
        )
    }

    @Test
    fun loadReminders_allReminderShow() {
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue().size,
            CoreMatchers.equalTo(3)
        )
    }

    @Test
    fun loadReminders_getError() {
        dataSource.setResponseError("error test")
        remindersListViewModel.loadReminders()
        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.equalTo("error test")
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}