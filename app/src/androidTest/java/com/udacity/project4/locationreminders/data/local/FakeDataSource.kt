package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(private var _listReminders: MutableList<ReminderDTO>) : ReminderDataSource {

    private var errorResponse: String = ""

    fun setResponseError(value: String = "") {
        errorResponse = value
    }

    fun setData(listReminders: MutableList<ReminderDTO>) {
        _listReminders = listReminders
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (errorResponse != "") Result.Error(errorResponse)
        else Result.Success(_listReminders)
    }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        _listReminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
    }


}