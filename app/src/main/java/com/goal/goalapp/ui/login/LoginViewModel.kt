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
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.dataStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class LoginViewModel(
    private val userRepository: UserRepository,
    private val sessionRepository: UserSessionRepository,
    application: Application
) : AndroidViewModel(application) {


    private val dataStore = application.applicationContext.dataStore

    //SharedFlow for navigation events in the Ui. Checks if the user is logged in
    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent: SharedFlow<Boolean> get() = _navigationEvent

    // keys for saving
    private val IS_USER_LOGGED_IN = booleanPreferencesKey("IS_USER_LOGGED_IN")
    private val SESSION_EXPIRY = longPreferencesKey("SESSION_EXPIRY")
    private val ACTIVE_USER_ID = intPreferencesKey("ACTIVE_USER_ID")


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
                //saveLoginStatus(true, session.expiresAt, user.id)
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
            val preferences = dataStore.data.first()
            val isLoggedIn = preferences[IS_USER_LOGGED_IN] ?: false
            val sessionExpiry = preferences[SESSION_EXPIRY] ?: 0L
            val userId = preferences[ACTIVE_USER_ID]

            _isLoggedIn.postValue(isLoggedIn)
            _sessionExpiry.postValue(sessionExpiry)
            _userId.postValue(userId)

            //so something can be done after all the previous things are done

            _navigationEvent.emit(_isLoggedIn.value == true && _sessionExpiry.value!! > System.currentTimeMillis())

        }
    }

    /**
     * Saves the login status to the DataStore
     */
    private fun saveLoginStatus(isLoggedIn: Boolean, sessionExpiry: Long, userId: Int) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[IS_USER_LOGGED_IN] = isLoggedIn
                preferences[SESSION_EXPIRY] = sessionExpiry
                preferences[ACTIVE_USER_ID] = userId
            }
        }
    }
}

sealed class LoginState {
    data object Initial : LoginState() // initial state
    data object Loading : LoginState() // when the login process is loading
    data class Success(val session: UserSession) : LoginState()
    data class Error(val message: String) : LoginState()
}

// Hashing des Passworts
fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}

fun verifyPassword(password: String, hashedPassword: String): Boolean {
    val result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword)
    return result.verified
}
