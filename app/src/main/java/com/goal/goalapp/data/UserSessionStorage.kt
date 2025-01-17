package com.goal.goalapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserSessionStorage(private val dataStore: DataStore<Preferences>) {

    val userIdFlow: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[ACTIVE_USER_ID]
    }

    companion object{
        // keys for saving the login status in the DataStore
        private val IS_USER_LOGGED_IN = booleanPreferencesKey("IS_USER_LOGGED_IN")
        private val SESSION_EXPIRY = longPreferencesKey("SESSION_EXPIRY")
        private val ACTIVE_USER_ID = intPreferencesKey("ACTIVE_USER_ID")
    }

    /**
     * Saves the login status to the DataStore
     */
    suspend fun saveLoginStatus(isLoggedIn: Boolean, sessionExpiry: Long, userId: Int) {
        dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = isLoggedIn
            preferences[SESSION_EXPIRY] = sessionExpiry
            preferences[ACTIVE_USER_ID] = userId
        }

    }

    /**
     * Loads the login status from the DataStore
     */
    suspend fun loadLoginStatus(): UserSession {

        val preferences = dataStore.data.first()
        val userSession = UserSession(
        isLoggedIn = preferences[IS_USER_LOGGED_IN] ?: false,
            expiresAt = preferences[SESSION_EXPIRY] ?: 0L,
        userId = preferences[ACTIVE_USER_ID]
        )
        return userSession
    }

    /**
     * Clears the login status from the DataStore
     */
    suspend fun clearLoginStatus() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

data class UserSession(
    val userId: Int?,
    val expiresAt: Long,
    val isLoggedIn: Boolean
)

