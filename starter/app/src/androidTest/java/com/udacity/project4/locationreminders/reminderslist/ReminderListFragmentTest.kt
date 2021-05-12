package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.base.AuthenticationState
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeReminderSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderListFragmentTest {
    private lateinit var application: Application
    private lateinit var dataSource: FakeReminderSource

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        dataSource = FakeReminderSource()
        stopKoin()
        application = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel{
                RemindersListViewModel(application, dataSource)
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single {LocalDB.createRemindersDao(application)}
        }
        startKoin {
            modules(listOf(myModule))
        }

    }

    @After
    fun close(){
        stopKoin()
    }


    @Test
    fun erroMesageTest_catchErrorMsg()= mainCoroutineRule.runBlockingTest {
        dataSource.saveReminder(ReminderDTO("title", "des", "location", 1.00, 2.00, "id" ))
        dataSource.deleteAllReminders()
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        delay(3000)


        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun reminderItemTest_reminderDisplayed()= mainCoroutineRule.runBlockingTest{
        val reminder = ReminderDTO("title", "des", "location", 1.00, 2.00, "id" )
        dataSource.saveReminder(reminder)
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        delay(5000)
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withText(reminder.title)).check(matches(isDisplayed()))
    }

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



//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
}