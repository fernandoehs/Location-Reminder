package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
    var returnError = false

    private val fakeData = hashMapOf<String, ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        if (!returnError)
            Result.Success(fakeData.values.toList())
        else
            Result.Error("Fake Error get all")

    override suspend fun saveReminder(reminder: ReminderDTO) {
        fakeData.put(reminder.id, reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        try {
            val reminder = fakeData[id]
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }

    override suspend fun deleteAllReminders() {
        fakeData.clear()
    }
}


