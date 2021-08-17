package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource

//6-Fixed
class FakeDataSource (private val list: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    var returnError = false


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError){
            return Result.Error("Test Exception")
        }
        list?.let { return Result.Success(ArrayList(it)) }
        return Result.Error("Reminders not found")
    }




    override suspend fun saveReminder(reminder: ReminderDTO) {
        list?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val item = list?.firstOrNull { it.id == id }
        item?.let {
            return Result.Success(it)
        } ?: run {
            return Result.Error("Not found")
        }
    }


    override suspend fun deleteAllReminders() {
        list?.clear()
    }
}


