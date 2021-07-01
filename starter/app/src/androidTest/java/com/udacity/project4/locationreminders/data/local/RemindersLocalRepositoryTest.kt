package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.FakeData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @Test
    suspend fun saveReminder_Inserted_WithSuccess() {
        val reminder = FakeData.reminders[0]

        repository.saveReminder(reminder)
        val result = repository.getReminder(reminder.id)

        val savedReminder = result as Result.Success

        savedReminder?.data.apply {
            assert(id == reminder.id)
            assert(title == reminder.title)
            assert(description == reminder.description)
            assert(location == reminder.location)
            assert(latitude == reminder.latitude)
            assert(longitude == reminder.longitude)
        }
    }

    @Test
    suspend fun getRemider_NoItemsExist_ReturnsError() {
        val result = repository.getReminder("0")
        assert(result is Result.Error)

        val reminderResult = result as Result.Error
        assert(reminderResult.message == "Reminder not found!")
    }

}