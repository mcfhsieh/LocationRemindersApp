package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Query
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase
    private lateinit var reminder: ReminderDTO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        reminder = ReminderDTO("title", "description", "location", 1.0, 2.0, "id")

    }

    @After
    fun destroy() {
        database.close()
    }

    @Test
    fun getReminders_loadRemindersFromDB() = runBlockingTest {

        database.reminderDao().saveReminder(reminder)
        val remindersFromDB = database.reminderDao().getReminders()
        assertThat(remindersFromDB, notNullValue())
        assertThat(remindersFromDB[0].title, `is`("title"))
    }

    @Test
    fun getReminderById_queryReminder() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)

        val queryreminder = database.reminderDao().getReminderById("id")

        assertThat(queryreminder, `is`(reminder))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()

        val reminders = database.reminderDao().getReminders()

        assertThat(reminders, `is`(emptyList()))
    }

//    TODO: Add testing implementation to the RemindersDao.kt*/

}