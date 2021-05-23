package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {
    // Extended Koin Test - embed autoclose @after method to close Koin after every test
    @get:Rule
    var activityRule: ActivityTestRule<RemindersActivity> =
        ActivityTestRule(RemindersActivity::class.java)

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var testViewModel: RemindersListViewModel
    private lateinit var fireBaseAuth: FirebaseAuth
    private var reminderSavedToastString: String = ""
    private var geofeceActiveToastString: String = ""


    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        fireBaseAuth = FirebaseAuth.getInstance()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()
        testViewModel = get()
        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Before
    fun registerIdling() {
        fireBaseAuth.signInWithEmailAndPassword("final@test.io", "testtesttest")
        reminderSavedToastString = appContext.getString(R.string.reminder_saved)
        geofeceActiveToastString = "Geofence Activated"
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdling() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Test
    fun createReminder_showSnackbars_showToasts_addReminderToList() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // Go to saveReminder
        onView(withId(R.id.addReminderFAB)).perform(click())
        //Save Reminder
        onView(withId(R.id.saveReminder)).perform(click())
        //Show Title Snackbar
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            matches(
                withText(
                    appContext.getString(R.string.err_enter_title)
                )
            )
        )
        runBlocking {
            delay(3000)
        }
        onView(withId(R.id.reminderTitle)).perform(replaceText("new title"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("new description"))
        onView(withId(R.id.saveReminder)).perform(click())
        //Show Location Snackbar
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            matches(
                withText(
                    appContext.getString(R.string.err_select_location)
                )
            )
        )
        runBlocking {
            delay(3000)
        }
        //Select Reminder Location
        onView(withId(R.id.selectLocation)).perform(click())
        onView(withId(R.id.map_fragment)).perform(longClick())
        onView(withId(R.id.save_location)).perform(click())

        //Save Reminder with title & location
        onView(withId(R.id.saveReminder)).perform(click())

        //Check Geofence Toast
        onView(withText(geofeceActiveToastString)).inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(
                matches(
                    isDisplayed()
                )
            )
        runBlocking {
            delay(2000)
        }
        //Check if new Reminder is displayed in ReminderList
        onView(withText("new title")).check(matches(isDisplayed()))
        onView(withText("new description")).check(matches(isDisplayed()))
        //Check Reminder Saved Toast
        onView(withText(reminderSavedToastString)).inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(
                matches(
                    isDisplayed()
                )
            )
        activityScenario.close()
    }
//    Done: add End to End testing to the app

}
