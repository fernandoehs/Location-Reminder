package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
private val reminders = mutableListOf<ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Return the reminders")
        Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("save the reminder")
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
        val reminder = reminders.find { r -> r.id == id }
        if (reminder != null) {
            return Result.Success(reminder)
        }

        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
        reminders.clear()
    }


}