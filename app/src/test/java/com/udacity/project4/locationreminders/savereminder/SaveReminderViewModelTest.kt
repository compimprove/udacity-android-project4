package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

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
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun validateEnteredData_errorEmptyTitle() {
        saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                "",
                "Description1",
                "Location1",
                1.0,
                2.0,
                "id1"
            )
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.equalTo(R.string.err_enter_title)
        )
    }

    @Test
    fun validateEnteredData_errorEmptyLocation() {
        saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                "Title1",
                "Description1",
                "",
                1.0,
                2.0,
                "id1"
            )
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.equalTo(R.string.err_select_location)
        )
    }

    @Test
    fun saveReminder_success() {
        val reminder = ReminderDataItem(
            "Title1",
            "Description1",
            "Location1",
            1.0,
            2.0,
            "id1"
        )
        saveReminderViewModel.saveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.savingReminderSuccess.getOrAwaitValue(),
            CoreMatchers.equalTo(reminder)
        )
    }

    @Test
    fun saveReminder_loadingEvent() {
        mainCoroutineRule.pauseDispatcher()
        val reminder = ReminderDataItem(
            "Title1",
            "Description1",
            "Location1",
            1.0,
            2.0,
            "id1"
        )
        saveReminderViewModel.saveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.equalTo(true)
        )

        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.equalTo(false)
        )
    }
}