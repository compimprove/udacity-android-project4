package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private var listReminders: MutableList<ReminderDTO>) : ReminderDataSource {

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
        if (errorResponse != "") return Result.Error(errorResponse)
        val reminder = listReminders.find { it.id == id }
        return if (reminder == null) {
            Result.Error("Reminder not found!")
        } else {
            Result.Success(reminder)
        }
    }

    override suspend fun deleteAllReminders() {
        listReminders = mutableListOf()
    }

}