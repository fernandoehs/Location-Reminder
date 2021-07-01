package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.FakeData
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
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        this.database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    suspend fun insert_whenValidRecord_IsAccessibleOnTable() {
        val reminder = FakeData.reminders[0]

        database.reminderDao().saveReminder(reminder)
        val savedReminder = database.reminderDao().getReminderById(reminder.id)

        savedReminder?.apply {
            assert(id == reminder.id)
            assert(title == reminder.title)
            assert(description == reminder.description)
            assert(location == reminder.location)
            assert(latitude == reminder.latitude)
            assert(longitude == reminder.longitude)
        }
    }

    @Test
    suspend fun delete_whenItemsExist_Then_AreCleared() {
        val repository = database.reminderDao()
        repository.saveReminder(FakeData.reminders[0])
        repository.saveReminder(FakeData.reminders[1])

        val existingReminders = repository.getReminders()

        assert(existingReminders.size == 2)

        repository.deleteAllReminders()

        val afterDeleteSize = repository.getReminders().size

        assert(afterDeleteSize == 0)
    }

}