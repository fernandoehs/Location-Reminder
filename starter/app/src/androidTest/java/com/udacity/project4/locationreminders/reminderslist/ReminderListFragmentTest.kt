package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext()
        stopKoin()

        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }

        startKoin {
            modules(listOf(myModule))
        }

        repository = get()

    }

    private fun seed() = runBlocking {
        val reminder1 = ReminderDTO("Porto", "Going to Porto", "Oporto", 30.000, 30.000, "PortoId")
        val reminder2 = ReminderDTO("Lisboa", "Going to Lisbon", "Lisbon", 31.000, 31.000, "LisboaId")
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
    }

    @Test
    fun navigationWhenAddReminderNavigateToSaveReminderFragment() {
        val navigationMock = mock(NavController::class.java)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.view!!, navigationMock)
        }

        Espresso.onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())

        verify(navigationMock).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun whenDataExistsThenDataIsCorrectlyDisplayedInFragment() {
        seed()
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        Espresso.onView(ViewMatchers.withId(R.id.reminderssRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Porto"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Lisboa"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun whenThereIsNoDataThenNoDataStringIsShown() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        Espresso.onView(ViewMatchers.withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText(appContext.getString(R.string.no_data))))
    }
}