package com.udacity.project4.authentication

import android.app.Application
import androidx.lifecycle.map
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.utils.FirebaseUserLiveData

class AuthenticationViewModel(app: Application,private val dataSource: ReminderDataSource) : BaseViewModel(app) {
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}