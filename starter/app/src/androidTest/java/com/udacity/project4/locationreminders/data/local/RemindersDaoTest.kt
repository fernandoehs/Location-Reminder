package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.FakeData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        this.database = Room.inMemoryDatabaseBuilder(
                getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    @Test
   fun insert_whenValidRecord_IsAccessibleOnTable() = runBlockingTest{

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
    fun delete_whenItemsExist_Then_AreCleared() = runBlockingTest {

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

