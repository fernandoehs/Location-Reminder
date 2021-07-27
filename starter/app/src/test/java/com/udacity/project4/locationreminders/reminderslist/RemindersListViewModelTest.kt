package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setupStatisticsViewModel() {
        // stop koin
        stopKoin()
        // Initialise the repository with no tasks.
        dataSource = FakeDataSource()
        val applicationMock = Mockito.mock(Application::class.java)
        remindersListViewModel = RemindersListViewModel(applicationMock, dataSource)
    }

    @Test
    fun loadingIndicator_hidingWhenUpdated() = mainCoroutineRule.runBlockingTest {
        // Given
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        // When
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()

        // Then
        MatcherAssert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun loadReminders_dataExists() = mainCoroutineRule.runBlockingTest {
        // Given
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        dataSource.returnError = false

        // When
        dataSource.saveReminder(data)
        remindersListViewModel.loadReminders()

        // Then
        Assert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue().firstOrNull { it.id == data.id }?.id,
            CoreMatchers.`is`(data.id)
        )
    }

    @Test
    fun loadReminders_dataNotExists() = mainCoroutineRule.runBlockingTest {
        // Given
        val data = ReminderDTO(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )
        dataSource.returnError = true

        // When
        dataSource.saveReminder(data)
        remindersListViewModel.loadReminders()

        // Then
        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("test error")
        )
    }

}