package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito
import org.robolectric.annotation.Config
import java.util.*

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setupStatisticsViewModel() {
        // stop koin
        stopKoin()
        // Initialise the repository with no tasks.
        dataSource = FakeDataSource()
        val applicationMock = Mockito.mock(Application::class.java)
        saveReminderViewModel = SaveReminderViewModel(applicationMock, dataSource)
    }

    @Test
    fun saveReminder_checkSuccess() {
        // Given
        val data = ReminderDataItem(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )

        // When
        saveReminderViewModel.saveReminder(data)

        // Then
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            Matchers.`is`(false)
        )
        Assert.assertEquals(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            NavigationCommand.Back
        )
    }


    @Test
    fun onClear_success() = mainCoroutineRule.runBlockingTest {
        // Given
        saveReminderViewModel.reminderTitle.value = "test title"
        saveReminderViewModel.reminderDescription.value = "test description"
        saveReminderViewModel.locationSelected(LatLng(0.0,0.0))

        // When
        saveReminderViewModel.onClear()

        // Then
        Assert.assertEquals(saveReminderViewModel.reminderTitle.getOrAwaitValue(), null)
        Assert.assertEquals(saveReminderViewModel.reminderDescription.getOrAwaitValue(), null)
        Assert.assertEquals(saveReminderViewModel.latLng.getOrAwaitValue(), null)
        Assert.assertEquals(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            null
        )
    }

    @Test
    fun reminderValidator_success() = mainCoroutineRule.runBlockingTest {
        // Given
        val data = ReminderDataItem(
            title = "test",
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )

        // When
        val validateResult = saveReminderViewModel.validateEnteredData(data)

        // Then
        Assert.assertEquals(validateResult, true)
    }

    @Test
    fun reminderValidatorEmptyTitle_errorSnackbar() = mainCoroutineRule.runBlockingTest {
        // Given
        val data = ReminderDataItem(
            title = null,
            description = "test desc",
            location = "test location",
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )

        // When
        saveReminderViewModel.validateEnteredData(data)

        // Then
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            Matchers.`is`(R.string.err_enter_title)
        )
    }

    @Test
    fun reminderValidatorEmptyLocation_errorSnackbar() = mainCoroutineRule.runBlockingTest {
        // Given
        val data = ReminderDataItem(
            title = "test",
            description = "test desc",
            location = null,
            latitude = 0.0,
            longitude = 0.0,
            id = UUID.randomUUID().toString()
        )

        // When
        saveReminderViewModel.validateEnteredData(data)

        // Then
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            Matchers.`is`(R.string.err_select_location)
        )
    }

}