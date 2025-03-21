package com.goal.goalapp.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.goal.goalapp.GoalApplication
import com.goal.goalapp.ui.calender.CalendarViewModel
import com.goal.goalapp.ui.group.CreateEditGroupViewModel
import com.goal.goalapp.ui.group.GroupViewModel
import com.goal.goalapp.ui.goal.CreateGoalViewModel
import com.goal.goalapp.ui.goal.GoalDetailsViewModel
import com.goal.goalapp.ui.goal.GoalOverviewViewModel
import com.goal.goalapp.ui.goal.RoutineDetailsViewModel
import com.goal.goalapp.ui.group.GroupChatViewModel
import com.goal.goalapp.ui.group.GroupDetailsViewModel
import com.goal.goalapp.ui.login.LoginViewModel
import com.goal.goalapp.ui.login.RegisterViewModel
import com.goal.goalapp.ui.settings.SettingsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire goal app
 */
object AppViewModelProvider{
    val Factory = viewModelFactory {

        // Initializer for LoginViewModel
        initializer {
            LoginViewModel(
                goalApplication().container.userRepository,
                goalApplication().container.userSessionRepository,
                goalApplication().container.userSessionStorage
            )
        }

        // Initializer for RegisterViewModel
        initializer {
            RegisterViewModel(
                goalApplication().container.userRepository
            )
        }

        // Initializer for GoalOverviewViewModel
        initializer {
            GoalOverviewViewModel(
                goalApplication().container.goalRepository,
                goalApplication().container.userSessionStorage
            )
        }

        // Initializer for CreateGoalViewModel
        initializer {
            CreateGoalViewModel(
                goalApplication().container.goalRepository,
                goalApplication().container.userSessionStorage
            )
        }

        initializer {
            GoalDetailsViewModel(
                goalApplication().container.goalRepository,
                goalApplication().container.userSessionStorage,
                goalApplication().container.postRepository,
                goalApplication().container.groupRepository
            )
        }

        initializer {
            RoutineDetailsViewModel(
                goalApplication().container.goalRepository
            )
        }

        initializer {
            CalendarViewModel(
                goalApplication().container.goalRepository,
                goalApplication().container.userSessionStorage
            )
        }

        initializer {
            GroupViewModel(
                goalApplication().container.groupRepository,
                goalApplication().container.userSessionStorage
            )
        }

        initializer {
            CreateEditGroupViewModel(
                goalApplication().container.groupRepository,
                goalApplication().container.userSessionStorage
            )
        }

        initializer {
            GroupChatViewModel(
                goalApplication().container.groupRepository,
                goalApplication().container.userSessionStorage,
                goalApplication().container.goalRepository,
                goalApplication().container.postRepository,
            )
        }

        initializer {
            GroupDetailsViewModel(
                goalApplication().container.groupRepository,
                goalApplication().container.userSessionStorage
            )
        }

        initializer {
            SettingsViewModel(
                goalApplication().container.userSessionStorage,
                goalApplication().container.userRepository
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