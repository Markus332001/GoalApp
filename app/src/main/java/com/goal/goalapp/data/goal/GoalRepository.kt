package com.goal.goalapp.data.goal

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
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
     * Insert completion criterion in the data source
     */
    suspend fun insertCompletionCriterion(completionCriterion: CompletionCriterion): Long

    /**
     * Insert routines in the data source
     */
    suspend fun insertRoutines(routines: List<Routine>)

    /**
     * Insert goal with details in the data source
     */
    @Transaction
    suspend fun insertGoalWithDetails(goal: Goal, completionCriteria: CompletionCriterion, routines: List<Routine>): Long

    /**
     * Update goal in the data source
     */
    suspend fun update(goal: Goal)

    /**
     * Update goal with details in the data source
     */
    suspend fun updateGoalWithDetails(goalWithDetails: GoalWithDetails)

    /**
     * Update completion criterion in the data source
     */
    suspend fun updateCompletionCriterion(completionCriterion: CompletionCriterion)

    /**
     * Update routines in the data source
     */
    suspend fun updateRoutine(routine: Routine)

    /**
     * Update routines in the data source
     */
    suspend fun updateRoutines(routines: List<Routine>)

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

    /**
     * Retrieve an completion criterion from the data source that match with the provided id
     */
    fun getCompletionCriterionById(completionCriterionId: Int): CompletionCriterion?

    /**
     * Retrieve an routine from the data source that match with the provided id
     */
    fun getRoutineById(routineId: Int): Routine?

}