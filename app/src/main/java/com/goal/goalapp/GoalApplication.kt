package com.goal.goalapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.goal.goalapp.data.AppContainer
import com.goal.goalapp.data.AppDataContainer


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class GoalApplication: Application() {


    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}