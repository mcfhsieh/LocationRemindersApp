package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    lateinit var dataSource: FakeDataSource
    lateinit var viewModel: RemindersListViewModel
    lateinit var reminder: ReminderDTO
    private val dispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        val viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        val reminder = ReminderDTO(
            "title",
            "description",
            "location",
            20.0,
            10.0
        )
        Dispatchers.setMain(dispatcher)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun loadReminders_() {
        runBlockingTest {
            dataSource.saveReminder(reminder)
            viewModel.loadReminders()

            val remindersList = viewModel.remindersList.getOrAwaitValue()[0]

            assertThat(remindersList, `is`(notNullValue()))
        }
    }

    //TODO: provide testing to the RemindersListViewModel and its live data objects

}