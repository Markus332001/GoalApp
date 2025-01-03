package com.goal.goalapp.data.goal

import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [Goal] from a given data source.
 */
interface GoalRepository {
    /**
     * Insert goal in the data source
     */
    suspend fun insertGoal(goal: Goal): Long

    /**
     * Update goal in the data source
     */
    suspend fun update(goal: Goal)

    /**
     * Delete goal from the data source
     */
    suspend fun deleteGoal(goal: Goal)

    /**
     * Retrieve an goal from the data source that match with the provided id
     */
    fun getGoalByIdStream(goalId: Int): Flow<Goal?>

    /**
     * Retrieve an goal with details from the data source that match with the provided id
     */
    fun getGoalWithDetailsByIdStream(goalId: Int): Flow<GoalWithDetails?>
}