package com.goal.goalapp.data.goal

import kotlinx.coroutines.flow.Flow

//override the functions defined in the GoalRepository interface and call the corresponding functions from the GoalDao
class OfflineGoalRepository(private val goalDao: GoalDao) : GoalRepository  {

    override suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)

    override suspend fun update(goal: Goal) = goalDao.update(goal)

    override suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    override fun getGoalByIdStream(goalId: Int): Flow<Goal?> = goalDao.getGoalById(goalId)

    override fun getGoalWithDetailsByIdStream(goalId: Int): Flow<GoalWithDetails?> = goalDao.getGoalWithDetailsById(goalId)
}