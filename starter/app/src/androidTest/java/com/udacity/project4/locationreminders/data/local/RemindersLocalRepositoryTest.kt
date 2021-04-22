package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private val reminder1 = ReminderDTO("1", "1", "1", 50.0, 40.0)
    private val reminder2 = ReminderDTO("2", "2", "2", 50.0, 40.0)
    private val reminder3 = ReminderDTO("3", "3", "3", 50.0, 40.0)
    private val reminders = mutableListOf(reminder1, reminder2, reminder3)
    private val remindersLocal = mutableListOf(reminder3)
    private val fakeDBDataSource = FakeDataSource(remindersLocal)
    private lateinit var repo: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun buildIt() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repo = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun destroyIt() {
        database.close()
    }

    @Test
    fun getReminders_getAllRemindersFromDB() = runBlocking{
        repo.saveReminder(reminder1)
        repo.saveReminder(reminder2)
        repo.saveReminder(reminder3)
        val allReminders = repo.getReminders() as Result.Success
        assertThat(allReminders.data[0], IsEqual(reminder1))
        val localReminder2 = repo.getReminder(reminder2.id) as Result.Success
        assertThat(localReminder2.data, IsEqual(reminder2))
    }

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

}