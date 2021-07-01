package com.udacity.project4

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

object FakeData {
    val reminders = arrayListOf(
        ReminderDTO(
            "Porto1",
            "TheRestaurantIMissEating",
            "Aliados",
            41.0000,
            -8.0000
        ),
        ReminderDTO(
            "Porto2",
            "ThePastelDeNata",
            "Faria Guuimaraes",
            41.0050,
            -8.0050
        ),
        ReminderDTO(
            "Porto3",
            "Office",
            "Rua Antonio Neves",
            41.0080,
            -8.0090
        )
    )

    val reminder =
        ReminderDataItem(
            "Porto1",
            "TheRestaurantIMissEating",
            "Aliados",
            41.0000,
            -8.0000
        )

}