package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import androidx.test.espresso.assertion.ViewAssertions.matches
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.local.FakeReminderSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.koin.core.context.GlobalContext.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.mock.getDefinition
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {
    private lateinit var application: Application
    private lateinit var dataSource: FakeReminderSource

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        stopKoin()
        dataSource = FakeReminderSource()
        application = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel{
                RemindersListViewModel(application, get())
            }
            single {RemindersLocalRepository(get()) as ReminderDataSource }
            single {LocalDB.createRemindersDao(application)}
        }
        startKoin {
            modules(listOf(myModule))
        }

    }

//    @Test
//    fun displayReminderItemTest_showReminderItemInList() = runBlocking{
//        dataSource.saveReminder(ReminderDTO("title", "des", "location", 1.00, 2.00, "id" ))
//
//        onView(withText("title")).check(matches(isDisplayed()))
//    }

    @Test
    fun navigationTest_RemindersList_to_SaveReminder() {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun erroMesageTest_catchErrorMsg()= mainCoroutineRule.runBlockingTest {
        dataSource.saveReminder(ReminderDTO("title", "des", "location", 1.00, 2.00, "id" ))
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        delay(3000)
       // dataSource.deleteAllReminders()

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
}