package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // Given
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        database.reminderDao().saveReminder(data)

        // When
        val loaded = database.reminderDao().getReminderById(data.id)

        // Then
        MatcherAssert.assertThat<ReminderDTO>(loaded as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(data.id))
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`(data.title))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`(data.description))
        MatcherAssert.assertThat(loaded.location, CoreMatchers.`is`(data.location))
        MatcherAssert.assertThat(loaded.latitude, CoreMatchers.`is`(data.latitude))
        MatcherAssert.assertThat(loaded.longitude, CoreMatchers.`is`(data.longitude))
    }

    @Test
    fun updateReminderAndGetById() = runBlockingTest {
        // Given
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        database.reminderDao().saveReminder(data)

        // When
        data.title = "changedTitle"
        database.reminderDao().saveReminder(data)

        // Then
        val loaded = database.reminderDao().getReminderById(data.id)
        MatcherAssert.assertThat(loaded?.title, CoreMatchers.`is`(data.title))
    }

}