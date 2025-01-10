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
    suspend fun updateCompletionCriteria(completionCriteria: CompletionCriterion)

    @Update
    suspend fun updateRoutine(routine: Routine)

    @Update
    suspend fun updateRoutines(routines: List<Routine>)

    @Update
    suspend fun updateRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalById(goalId: Int): Flow<Goal?>

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
}