package com.goal.goalapp.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.goal.goalapp.TIMEOUT_MILLIS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class GoalOverviewViewModel(
    private val goalRepository: GoalRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {


    @OptIn(ExperimentalCoroutinesApi::class)
    val goals: StateFlow<List<Goal>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            goalRepository.getGoalsByUserIdStream(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )
}