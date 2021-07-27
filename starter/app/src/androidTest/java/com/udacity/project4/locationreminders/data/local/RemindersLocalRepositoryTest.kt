package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun onClear() {
        stopKoin()
        database.close()
    }


    @Test
    fun saveReminder_retrieveReminder() = runBlocking {
        // Give
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        repository.saveReminder(data)

        // When
        val result = repository.getReminder(data.id)

        // Then
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`(data.title))
        assertThat(result.data.description, `is`(data.description))
        assertThat(result.data.location, `is`(data.location))
        assertThat(result.data.latitude, `is`(data.latitude))
        assertThat(result.data.longitude, `is`(data.longitude))
    }

    @Test
    fun deleteReminders_retrieveReminder() = runBlocking {
        // Give
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        repository.saveReminder(data)

        // When
        repository.deleteAllReminders()
        val result = repository.getReminder(data.id)

        // Then
        assertThat(result.succeeded, `is`(false))
    }

}