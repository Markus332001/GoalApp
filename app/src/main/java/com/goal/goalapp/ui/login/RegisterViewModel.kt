package com.goal.goalapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.goal.goalapp.data.user.User
import com.goal.goalapp.data.user.UserRepository
import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> get() = _registerState

    fun register(username: String, email: String, password: String) {
        _registerState.value = RegisterState.Loading
        val hashedPassword = hashPassword(password)
        viewModelScope.launch {
            try {
                val newUser: User = User(username, email, hashedPassword)
                userRepository.insertUser(newUser)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error(e.message ?: "Unknown error")
            }
        }

    }

}

// Hash Password
fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

sealed class RegisterState {
    data object Initial : RegisterState() // initial state
    data object Loading : RegisterState() // when the login process is loading
    data object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}