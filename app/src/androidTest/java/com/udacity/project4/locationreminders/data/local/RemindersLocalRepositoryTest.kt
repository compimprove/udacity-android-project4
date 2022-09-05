package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var localDataSource: RemindersLocalRepository


    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            AuthUI.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveAndGetReminder() = runBlocking {
        // GIVEN
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")

        localDataSource.saveReminder(reminder)

        // WHEN
        val result = localDataSource.getReminder(reminder.id)

        // THEN
        if (result is Result.Success<ReminderDTO>) {
            val reminderSaved = result.data
            assertThat<ReminderDTO>(reminderSaved as ReminderDTO, CoreMatchers.notNullValue())
            assertThat(reminderSaved.id, `is`(reminder.id))
            assertThat(reminderSaved.title, `is`(reminder.title))
            assertThat(reminderSaved.description, `is`(reminder.description))
            assertThat(reminderSaved.latitude, `is`(reminder.latitude))
            assertThat(reminderSaved.longitude, `is`(reminder.longitude))
            assertThat(reminderSaved.location, `is`(reminder.location))
        }
    }

    @Test
    fun deleteAllReminder() = runBlocking {
        // GIVEN
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")

        localDataSource.saveReminder(reminder)

        // WHEN
        localDataSource.deleteAllReminders()
        val result = localDataSource.getReminders()

        // THEN
        assertThat(result is Result.Success<List<ReminderDTO>>, `is`(true))
        if (result is Result.Success<List<ReminderDTO>>) {
            assertThat(result.data.size, `is`(0))
        }
    }

    @Test
    fun getReminder_notFound() = runBlocking {
        // GIVEN
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")
        localDataSource.saveReminder(reminder)

        // WHEN
        val result = localDataSource.getReminder("id2")

        // THEN
        assertThat(result is Result.Error, `is`(true))
        assertThat((result as Result.Error).message, `is`("Reminder not found!"))
    }
}