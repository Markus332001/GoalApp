package com.goal.goalapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSessionStorage: UserSessionStorage,
    private val userRepository: UserRepository
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val user = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            userRepository.getUserByIdStream(userId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    fun logout(){
        viewModelScope.launch {
            userSessionStorage.clearLoginStatus()
        }
    }

}