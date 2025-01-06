package com.goal.goalapp

import android.app.Application
import com.goal.goalapp.data.AppContainer
import com.goal.goalapp.data.AppDataContainer

const val TIMEOUT_MILLIS = 5_000L

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