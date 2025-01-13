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
     * Insert routine with calendar days in the data source
     */
    suspend fun insertRoutineWithCalendarDays(routineWithCalendarDays: RoutineWithCalendarDays, goalId: Int): Long

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
    suspend fun updateGoalWithDetails(goalWithDetails: GoalWithDetails): Int

    /**
     * Update routine calendar days in the data source
     */
    suspend fun updateRoutineCalendarDay(routineCalendarDay: RoutineCalendarDays)

    /**
     * Update completion criterion in the data source
     */
    suspend fun updateCompletionCriterion(completionCriterion: CompletionCriterion): Int

    /**
     * Update routines in the data source
     */
    suspend fun updateRoutine(routine: Routine): Int

    /**
     * Update routine with calendar days in the data source
     */
    suspend fun updateRoutineWithCalendarDays(routineWithCalendarDays: RoutineWithCalendarDays): Int

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
    suspend fun deleteGoalById(goalId: Int)

    /**
     * Delete routine
     */
    suspend fun deleteRoutineById(routineId: Int)

    /**
     * Delete routine calendar days by Id
     */
    suspend fun deleteRoutineCalendarDaysByIds(routineCalendarDaysIds: List<Int>)

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

    /**
     * Retrieve all calendar days by the provided routine id
     */
    suspend fun getRoutineCalendarDaysByRoutineId(routineId: Int): List<RoutineCalendarDays>

    /**
     * Retrieve all routines by the provided goal id
     */
    suspend fun getRoutinesByGoalId(goalId: Int): List<Routine>

    /**
     * Retrieve all routines with calendar days by the provided goal id
     */
    fun getRoutinesWithCalendarDaysByGoalIdStream(goalId: Int): Flow<List<RoutineWithCalendarDays>>

    /**
     * Retrieve all routine calendar days by the provided user id
     */
    fun getRoutineWithCalendarDaysByUserIdStream(userId: Int): Flow<List<RoutineWithCalendarDays>>

    /**
     * Retrieve all goals by the provided user id
     */
    fun getGoalsByUserIdStream(userId: Int): Flow<List<Goal>>

}