package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.FakeData
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: FakeDataSource

    private lateinit var viewModel: SaveReminderViewModel

    private lateinit var context: Application

    @Before
    fun setup() {
        stopKoin()
        repository = FakeDataSource()
        context = ApplicationProvider.getApplicationContext()

        viewModel = SaveReminderViewModel(
            context,
            repository
        )
    }

    @Test
    fun savedReminderIsClearAndReturnsSuccess() {
        viewModel.saveReminder(FakeData.reminder)

        assert(viewModel.showToast.getOrAwaitValue() == "Reminder Saved !")

    }

    @Test
    fun savedReminderWhenTitleIsNullReturnsError() {
        val reminder = FakeData.reminder
        reminder.title = null
        viewModel.validateAndSaveReminder(reminder)

        assert(viewModel.showSnackBarInt.getOrAwaitValue() == R.string.err_enter_title)
    }

    @Test
    fun savedReminderWhenLocationIsInvalidReturnsError() {
        val reminder = FakeData.reminder
        reminder.location = null
        viewModel.validateAndSaveReminder(reminder)

        assert(viewModel.showSnackBarInt.getOrAwaitValue() == R.string.err_select_location)
    }

    @Test
    fun saveReminderWhenLoadingIsShown() = runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        viewModel.saveReminder(FakeData.reminder)

        assert(viewModel.showLoading.getOrAwaitValue() == true)

        mainCoroutineRule.resumeDispatcher()

        assert(viewModel.showLoading.getOrAwaitValue() == false)
    }
}
