package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var reminder: ReminderDTO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        dataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        reminder = ReminderDTO(
            "title",
            "description",
            "location",
            20.0,
            10.0
        )
        mainCoroutineRule.runBlockingTest {
            dataSource.saveReminder(reminder)
        }
    }

    @Test
    fun loadReminders_getsRemindersForDisplay() {

        viewModel.loadReminders()

        val remindersList = viewModel.remindersList.getOrAwaitValue()[0]

        assertThat(remindersList, `is`(notNullValue()))
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(false))
        stopKoin()
    }

    @Test
    fun loadReminders_testErrorHandling() {
        dataSource.activateErrorHandling(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("Reminder not found"))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
        stopKoin()
    }

    //TODO: provide testing to the RemindersListViewModel and its live data objects

}