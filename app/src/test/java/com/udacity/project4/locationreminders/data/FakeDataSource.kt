package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val listReminders: MutableList<ReminderDTO>) : ReminderDataSource {

    private var errorResponse: String = ""

    fun setResponseError(value: String = "") {
        errorResponse = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (errorResponse != "") Result.Error(errorResponse)
        else Result.Success(listReminders)
    }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        listReminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
    }


}