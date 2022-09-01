package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.gms.tasks.Task
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")

        database.reminderDao().saveReminder(reminder)

        // WHEN
        val reminderSaved = database.reminderDao().getReminderById(reminder.id)

        // THEN
        assertThat<ReminderDTO>(reminderSaved as ReminderDTO, notNullValue())
        assertThat(reminderSaved.id, `is`(reminder.id))
        assertThat(reminderSaved.title, `is`(reminder.title))
        assertThat(reminderSaved.description, `is`(reminder.description))
        assertThat(reminderSaved.latitude, `is`(reminder.latitude))
        assertThat(reminderSaved.longitude, `is`(reminder.longitude))
        assertThat(reminderSaved.location, `is`(reminder.location))

    }

    @Test
    fun deleteAllReminder() = runBlockingTest {
        // GIVEN
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 1.0, 2.0, "id1")

        database.reminderDao().saveReminder(reminder)

        // WHEN
        database.reminderDao().deleteAllReminders()
        val allReminders = database.reminderDao().getReminders()

        assertThat(allReminders.size, `is`(0))
    }
}