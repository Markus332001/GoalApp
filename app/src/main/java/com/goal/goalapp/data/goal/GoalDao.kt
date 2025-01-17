package com.goal.goalapp.data.goal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletionCriterion(completionCriterion: CompletionCriterion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<Routine>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>)

    @Update
    suspend fun update(goal: Goal):Int


    @Update
    suspend fun updateCompletionCriteria(completionCriteria: CompletionCriterion): Int

    @Update
    suspend fun updateRoutine(routine: Routine): Int

    @Update
    suspend fun updateRoutines(routines: List<Routine>)

    @Update
    suspend fun updateRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>)

    @Update
    suspend fun updateRoutineCalendarDay(routineCalendarDay: RoutineCalendarDays)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Int)

    @Query("DELETE FROM routine WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: Int)

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalById(goalId: Int): Flow<Goal?>

    @Query("DELETE FROM routinecalendardays WHERE id IN (:routineCalendarDaysIds)")
    suspend fun deleteRoutineCalendarDaysById(routineCalendarDaysIds: List<Int>)

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalWithDetailsById(goalId: Int): Flow<GoalWithDetails?>

    @Query("SELECT * FROM completioncriterion WHERE id = :completionCriterionId")
    fun getCompletionCriterionById(completionCriterionId: Int): CompletionCriterion?

    @Query("SELECT * FROM routine WHERE id = :routineId")
    fun getRoutineByIdStream(routineId: Int): Flow<Routine?>

    @Transaction
    @Query("SELECT * FROM routine WHERE id = :routineId")
    fun getRoutineWithCalenderDaysByIdStream(routineId: Int): Flow<RoutineWithCalendarDays?>

    @Query("SELECT * FROM routinecalendardays WHERE routineId = :routineId")
    suspend fun getRoutineCalendarDaysByRoutineId(routineId: Int): List<RoutineCalendarDays>

    @Query("SELECT * FROM routine WHERE goalId = :goalId")
    suspend fun getRoutinesByGoalId(goalId: Int): List<Routine>

    @Transaction
    @Query("SELECT * FROM routine WHERE goalId = :goalId")
    fun getRoutinesWithCalendarDaysByGoalIdStream(goalId: Int): Flow<List<RoutineWithCalendarDays>>

    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun getGoalsByUserIdStream(userId: Int): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * FROM goals WHERE userId = :userId")
    suspend fun getGoalsWithDetailsByUserId(userId: Int): List<GoalWithDetails>

}