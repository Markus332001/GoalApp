package com.goal.goalapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.goal.goalapp.GoalApplication
import com.goal.goalapp.ui.login.LoginViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire goal app
 */
object AppViewModelProvider{
    val Factory = viewModelFactory {
        // Initializer for LoginViewModel
        initializer {
            val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as? Application ?: throw IllegalStateException("Application context required")

            LoginViewModel(
                goalApplication().container.userRepository,
                goalApplication().container.userSessionRepository,
                application
            )
        }

    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [GoalApplication].
 */
fun CreationExtras.goalApplication(): GoalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as GoalApplication)