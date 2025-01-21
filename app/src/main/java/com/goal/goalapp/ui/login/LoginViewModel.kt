package com.goal.goalapp.ui.login

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.user.UserRepository
import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.launch
import at.favre.lib.crypto.bcrypt.BCrypt
import com.goal.goalapp.data.user_session.UserSessionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.LiveData
import com.goal.goalapp.data.UserSessionStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull


class LoginViewModel(
    private val userRepository: UserRepository,
    private val sessionRepository: UserSessionRepository,
    private val userSessionStorage: UserSessionStorage
) : ViewModel(){


    // LiveData for the login state, session expiry, and user ID
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _sessionExpiry = MutableLiveData<Long>()
    val sessionExpiry: LiveData<Long> get() = _sessionExpiry

    private val _userId = MutableLiveData<Int?>()
    val userId: LiveData<Int?> get() = _userId


    // StateFlow for the login state, which saves the last emitted value
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> get() = _loginState

    /**
     * Checks if the user is logged in and if the session is still valid. And then sends them to different Screens
     */


    // Login-Function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val user = userRepository.getUserByEmail(email)
            if (user != null && verifyPassword(password, user.passwordHash)) {
                val session = sessionRepository.createSession(user.id)
                _loginState.value = LoginState.Success(session)
                userSessionStorage.saveLoginStatus(true, session.expiresAt, user.id)
            } else {
                _loginState.value = LoginState.Error("Ungültige Anmeldedaten")
            }
        }
    }

    /**
     * Loads the login status from the DataStore
     */
    fun  loadLoginStatus() {
        viewModelScope.launch {
            val userSession = userSessionStorage.loadLoginStatus()

            _isLoggedIn.postValue(userSession.isLoggedIn)
            _sessionExpiry.postValue(userSession.expiresAt)
            _userId.postValue(userSession.userId)
        }
    }

    suspend fun isLoggedIn(): Boolean{
        if(isLoggedIn.value == true && (sessionExpiry.value?: 0) > System.currentTimeMillis() && userId.value != null){
            val userInDb = userRepository.getUserByIdStream(userId.value!!)
            if(userInDb.firstOrNull() != null){
                return true
            }
        }
        return false
    }

}

sealed class LoginState {
    data object Initial : LoginState() // initial state
    data object Loading : LoginState() // when the login process is loading
    data class Success(val session: UserSession) : LoginState()
    data class Error(val message: String) : LoginState()
}

fun verifyPassword(password: String, hashedPassword: String): Boolean {
    val result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword)
    return result.verified
}
