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
     * Insert routine in the data source
     */
    suspend fun insertRoutine(routine: Routine): Long

    /**
     * Insert routine calendar days in the data source
     */
    suspend fun insertRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>)

    /**
     * Insert goal with details in the data source
     */
    @Transaction
    suspend fun insertGoalWithDetails(goal: Goal, completionCriteria: CompletionCriterion, routinesWithCalendarDays: List<RoutineWithCalendarDays>): Long

    /**
     * Update goal in the data source
     */
    suspend fun update(goal: Goal): Int

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
     * Update routine calendar days in the data source
     */
    suspend fun updateRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>)

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
    fun getRoutineByIdStream(routineId: Int): Flow<Routine?>

    /**
     * Retrieve an routine with calendar days from the data source that match with the provided id
     */
    fun getRoutineWithCalendarDaysByIdStream(routineId: Int): Flow<RoutineWithCalendarDays?>

}