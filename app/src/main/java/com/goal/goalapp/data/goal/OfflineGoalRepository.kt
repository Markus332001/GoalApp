package com.goal.goalapp.data.goal

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

//override the functions defined in the GoalRepository interface and call the corresponding functions from the GoalDao
class OfflineGoalRepository(private val goalDao: GoalDao) : GoalRepository  {

    override suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    override suspend fun insertCompletionCriterion(completionCriterion: CompletionCriterion): Long = goalDao.insertCompletionCriterion(completionCriterion)

    override suspend fun insertRoutines(routines: List<Routine>) = goalDao.insertRoutines(routines)

    @Transaction
    override suspend fun insertGoalWithDetails(
        goal: Goal,
        completionCriteria: CompletionCriterion,
        routines: List<Routine>
    ): Long {
        val goalId = insertGoal(goal)
        /**
         * Sets the goalId of the completionCriterion to the goalId of the goal
         * Adds the completionCriterion to the database
         */
        val completionCriteriaDb = completionCriteria.copy(goalId = goalId.toInt())
        insertCompletionCriterion(completionCriteriaDb)

        /**
         * Sets the goalId of the routines to the goalId of the goal
         * Adds the routines to the database
         */
        val routinesDb: MutableList<Routine> = mutableListOf()
        routines.forEach{
            routinesDb.add(
                it.copy(goalId = goalId.toInt())
            )
        }
        insertRoutines(routinesDb)

        return goalId
    }

    override suspend fun update(goal: Goal) = goalDao.update(goal)

    override suspend fun updateGoalWithDetails(goalWithDetails: GoalWithDetails){
        update(goalWithDetails.goal)
        updateCompletionCriterion(goalWithDetails.completionCriteria)
        updateRoutines(goalWithDetails.routines)
    }

    override suspend fun updateCompletionCriterion(completionCriterion: CompletionCriterion) = goalDao.updateCompletionCriteria(completionCriterion)

    override suspend fun updateRoutine(routine: Routine) = goalDao.updateRoutine(routine)

    override suspend fun updateRoutines(routines: List<Routine>) = goalDao.updateRoutines(routines)

    override suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    override fun getGoalByIdStream(goalId: Int): Flow<Goal?> = goalDao.getGoalById(goalId)

    override fun getGoalWithDetailsByIdStream(goalId: Int): Flow<GoalWithDetails?> = goalDao.getGoalWithDetailsById(goalId)

    override fun getCompletionCriterionById(completionCriterionId: Int): CompletionCriterion? = goalDao.getCompletionCriterionById(completionCriterionId)

    override fun getRoutineById(routineId: Int): Routine? = goalDao.getRoutineById(routineId)
}