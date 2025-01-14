package com.goal.goalapp.data.goal

import androidx.room.Transaction
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.Frequency
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

//override the functions defined in the GoalRepository interface and call the corresponding functions from the GoalDao
class OfflineGoalRepository(private val goalDao: GoalDao) : GoalRepository  {

    override suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)

    override suspend fun insertCompletionCriterion(completionCriterion: CompletionCriterion): Long = goalDao.insertCompletionCriterion(completionCriterion)

    override suspend fun insertRoutines(routines: List<Routine>) = goalDao.insertRoutines(routines)

    override suspend fun insertRoutineWithCalendarDays(routineWithCalendarDays: RoutineWithCalendarDays, goalId: Int): Long {

        val newRoutineWithCalendarDays = routineWithCalendarDays.copy(
            calendarDays = createRoutineCalendarDays(routineWithCalendarDays = routineWithCalendarDays
                , startDate = routineWithCalendarDays.routine.startDate, targetValue = routineWithCalendarDays.routine.targetValue)
        )
        //sorts the days of week and sets the goalId
        val routine = newRoutineWithCalendarDays.routine.copy(goalId = goalId.toInt()
            , daysOfWeek =newRoutineWithCalendarDays.routine.daysOfWeek?.sortedBy { it.value })
        val routineId = insertRoutine(routine)

        val routineCalendarDays = newRoutineWithCalendarDays.calendarDays.map {
            it.copy(routineId = routineId.toInt())
        }
        insertRoutineCalendarDays(routineCalendarDays)
        return routineId
    }

    override suspend fun insertRoutine(routine: Routine): Long = goalDao.insertRoutine(routine)

    override suspend fun insertRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>) = goalDao.insertRoutineCalendarDays(routineCalendarDays)

    @Transaction
    override suspend fun insertGoalWithDetails(
        goal: Goal,
        completionCriteria: CompletionCriterion,
        routinesWithCalendarDays: List<RoutineWithCalendarDays>
    ): Long {
        val goalId = insertGoal(goal)
        /**
         * Sets the goalId of the completionCriterion to the goalId of the goal
         * Adds the completionCriterion to the database
         */
        val completionCriteriaDb = completionCriteria.copy(goalId = goalId.toInt())
        insertCompletionCriterion(completionCriteriaDb)

        /**
         * Sets the goalId of the routine to the goalId of the goal
         * Adds the routines to the database
         * And adds the routineCalendarDays to the database
         */
        for(routineWithCalendarDays in routinesWithCalendarDays){
            insertRoutineWithCalendarDays(routineWithCalendarDays, goalId.toInt())
        }

        return goalId
    }

    override suspend fun update(goal: Goal): Int = goalDao.update(goal)

    override suspend fun updateGoalWithDetails(goalWithDetails: GoalWithDetails): Int{

        val goalRoutinesDb: List<Routine> = getRoutinesByGoalId(goalWithDetails.goal.id)

        //deletes all routines from db which arent anymore in Update
        val toDeleteRoutine: List<Routine> = goalRoutinesDb.filter{dbRoutine ->
            goalWithDetails.routines.none { updatedRoutine ->
                updatedRoutine.routine.id == dbRoutine.id
            }
        }
        for(routine in toDeleteRoutine){
            deleteRoutineById(routine.id)
        }

        //gets all routines which arent in the database to insert
        //when no routines are in the db all routines have to be inserted
        val toInsertRoutine: List<RoutineWithCalendarDays> = if(goalRoutinesDb.size == 0)
            goalWithDetails.routines else
            goalWithDetails.routines.filter{updatedRoutine ->
                goalRoutinesDb.none { dbRoutine ->
                    dbRoutine.id == updatedRoutine.routine.id
                }
        }
        for(routine in toInsertRoutine){
            insertRoutineWithCalendarDays(routine, goalWithDetails.goal.id)
        }

        //gets all routines which are in both to update
        val toUpdateRoutines = goalWithDetails.routines.filter{
            goalRoutinesDb.any{r ->
                r.id == it.routine.id
            }
        }
        for(routine in toUpdateRoutines){
            updateRoutineWithCalendarDays(routine)
        }

        val routinesDb = getRoutinesByGoalId(goalId = goalWithDetails.goal.id)

        //updates the progress of the goal
        val newGoal = goalWithDetails.goal.copy(
            progress = calculateProgressGoal(goalWithDetails.goal, routinesDb, goalWithDetails.completionCriteria))

        val goalId: Int = update(newGoal)

        updateCompletionCriterion(goalWithDetails.completionCriteria)

        return goalId
    }

    override suspend fun updateRoutineCalendarDay(routineCalendarDay: RoutineCalendarDays) {
        goalDao.updateRoutineCalendarDay(routineCalendarDay)
        //calculates the progress of the routine
        var routineWithCalendarDays = getRoutineWithCalendarDaysByIdStream(routineCalendarDay.routineId).first()
        if(routineWithCalendarDays != null){
            routineWithCalendarDays = routineWithCalendarDays.copy(routine = routineWithCalendarDays.routine.copy(progress = calculateProgressRoutine(routineWithCalendarDays)))
            //updates the new routine
            goalDao.updateRoutine(routineWithCalendarDays.routine)
        }else{
            return
        }

        var goalWithDetails: GoalWithDetails? = getGoalWithDetailsByIdStream(routineWithCalendarDays.routine.goalId).first()

        if(goalWithDetails == null || goalWithDetails.completionCriteria.completionType != CompletionType.ConnectRoutine){
            return
        }
        //calculates the new progress, when it has the completion type connected routine
        goalWithDetails = goalWithDetails.copy(goal = goalWithDetails.goal.copy(
            progress = calculateProgressGoal(goalWithDetails.goal, goalWithDetails.routines.map{it.routine}, goalWithDetails.completionCriteria)))

        updateGoalWithDetails(goalWithDetails)

    }

    override suspend fun updateRoutineWithCalendarDays(routineWithCalendarDays: RoutineWithCalendarDays): Int{

        val routineDb = getRoutineByIdStream(routineWithCalendarDays.routine.id).first() ?: return 0

        //Compares the routine from the database and the one send in -> So when they are not the same it has to generate the calendar days new
        if(!compareRoutines(routineDb, routineWithCalendarDays.routine)){
            val newCalendarDays =  editRoutineCalendarDays(routineWithCalendarDays).map{ it.copy(routineId = routineWithCalendarDays.routine.id) }
            insertRoutineCalendarDays(newCalendarDays)
        }
        //gets all the calendar days by routine id
        val calendarDaysDb = getRoutineCalendarDaysByRoutineId(routineWithCalendarDays.routine.id)

        //updates the routine with the new progress
        val newRoutine = routineWithCalendarDays.routine.copy(
            progress = calculateProgressRoutine(routineWithCalendarDays.copy(calendarDays = calendarDaysDb)),
            daysOfWeek = routineWithCalendarDays.routine.daysOfWeek?.sortedBy { it.value }
            )
        val routineId: Int = updateRoutine(newRoutine)

        return routineId
    }


    override suspend fun updateCompletionCriterion(completionCriterion: CompletionCriterion): Int = goalDao.updateCompletionCriteria(completionCriterion)

    override suspend fun updateRoutine(routine: Routine): Int = goalDao.updateRoutine(routine)

    override suspend fun updateRoutines(routines: List<Routine>) = goalDao.updateRoutines(routines)

    override suspend fun updateRoutineCalendarDays(routineCalendarDays: List<RoutineCalendarDays>) = goalDao.updateRoutineCalendarDays(routineCalendarDays)

    override suspend fun deleteGoalById(goalId: Int) = goalDao.deleteGoalById(goalId)

    override suspend fun deleteRoutineById(routineId: Int) = goalDao.deleteRoutineById(routineId)

    override suspend fun deleteRoutineCalendarDaysByIds(routineCalendarDaysIds: List<Int>) = goalDao.deleteRoutineCalendarDaysById(routineCalendarDaysIds)

    override fun getGoalByIdStream(goalId: Int): Flow<Goal?> = goalDao.getGoalById(goalId)

    override fun getGoalWithDetailsByIdStream(goalId: Int): Flow<GoalWithDetails?> = goalDao.getGoalWithDetailsById(goalId)

    override fun getCompletionCriterionById(completionCriterionId: Int): CompletionCriterion? = goalDao.getCompletionCriterionById(completionCriterionId)

    override fun getRoutineByIdStream(routineId: Int): Flow<Routine?> = goalDao.getRoutineByIdStream(routineId)

    override fun getRoutineWithCalendarDaysByIdStream(routineId: Int): Flow<RoutineWithCalendarDays?> = goalDao.getRoutineWithCalenderDaysByIdStream(routineId)

    override suspend fun getRoutineCalendarDaysByRoutineId(routineId: Int): List<RoutineCalendarDays> = goalDao.getRoutineCalendarDaysByRoutineId(routineId)

    override suspend fun getRoutinesByGoalId(goalId: Int): List<Routine> = goalDao.getRoutinesByGoalId(goalId)

    override fun getRoutinesWithCalendarDaysByGoalIdStream(goalId: Int): Flow<List<RoutineWithCalendarDays>> = goalDao.getRoutinesWithCalendarDaysByGoalIdStream(goalId)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRoutineWithCalendarDaysByUserIdStream(userId: Int): Flow<List<RoutineWithCalendarDays>> {
        return getGoalsByUserIdStream(userId)
            .flatMapLatest { userGoals ->
                if (userGoals.isEmpty()) {
                    // returns an empty flow, when there are no goals
                    flowOf(emptyList())
                } else {
                    // create a list of flows for every goal
                    val routinesFlowList: List<Flow<List<RoutineWithCalendarDays>>> = userGoals.map { goal ->
                        getRoutinesWithCalendarDaysByGoalIdStream(goal.id)
                    }

                    // combines all flows of the routines
                    combine(routinesFlowList) { routinesWithCalendarDaysArray ->
                        routinesWithCalendarDaysArray.flatMap { it }
                    }
                }
            }
    }


    override fun getGoalsByUserIdStream(userId: Int): Flow<List<Goal>> = goalDao.getGoalsByUserIdStream(userId)


    private fun calculateProgressGoal(goal: Goal, routines: List<Routine>, completionCriterion: CompletionCriterion): Float{

        return when(completionCriterion.completionType){
            CompletionType.ReachGoal -> goal.progress
            CompletionType.ConnectRoutine -> routines.map{it.progress }.sum() / routines.size.toFloat()
            CompletionType.ReachTargetValue -> ((completionCriterion.currentValue?.toFloat()
                ?: 0f) / if(completionCriterion.targetValue == null) return 0f else completionCriterion.targetValue.toFloat())
        }
    }

    private fun calculateProgressRoutine(routineWithCalendarDays: RoutineWithCalendarDays): Float{
        val goalValue = if(routineWithCalendarDays.routine.endDate == null) routineWithCalendarDays.routine.targetValue?: return 0f else
            routineWithCalendarDays.calendarDays.size
        val completedDays = routineWithCalendarDays.calendarDays.count { it.isCompleted }
        return completedDays.toFloat() / goalValue.toFloat()
    }

    /**
     * Checks if there are already calendar days for the routine and
     * acts accordingly
     */
    private suspend fun editRoutineCalendarDays(routineWithCalendarDays: RoutineWithCalendarDays): List<RoutineCalendarDays>{

        //sets the startdate new when its in the past so it can generate the calendar days from the present day
        var newStartDate: LocalDate = routineWithCalendarDays.routine.startDate
        if(routineWithCalendarDays.routine.startDate < LocalDate.now()){
            newStartDate = LocalDate.now()
        }

        //deletes all calendar days whom are in the future
        val futureCalendarDays = routineWithCalendarDays.calendarDays.filter { it.date > LocalDate.now() }
        if(futureCalendarDays.isNotEmpty()){
            deleteRoutineCalendarDaysByIds(futureCalendarDays.map { it.id })
        }

        val areCompleted = routineWithCalendarDays.calendarDays.count { it.isCompleted }
        val newTargetValue = routineWithCalendarDays.routine.targetValue?.minus(areCompleted)

        //create only new calendar days, which are after today
        return createRoutineCalendarDays(
            routineWithCalendarDays = routineWithCalendarDays,
            startDate = newStartDate,
            targetValue = newTargetValue
        )
    }

    /**
     * Compares if the routine changed. So it has to generate the calendar days new
     */
    private fun compareRoutines(routine1: Routine, routine2: Routine): Boolean{
        return routine1.daysOfWeek == routine2.daysOfWeek &&
                routine1.frequency == routine2.frequency &&
                routine1.intervalDays == routine2.intervalDays &&
                routine1.startDate == routine2.startDate &&
                routine1.endDate == routine2.endDate &&
                routine1.targetValue == routine2.targetValue
    }

    private fun createRoutineCalendarDays(
        routineWithCalendarDays: RoutineWithCalendarDays,
        startDate: LocalDate,
        targetValue: Int?,
    ): List<RoutineCalendarDays>{
        val calendarDays = mutableListOf<RoutineCalendarDays>()

        val interval = when(routineWithCalendarDays.routine.frequency){
            Frequency.Daily -> 1
            Frequency.Weekly -> if(routineWithCalendarDays.routine.daysOfWeek == null) return emptyList() else null
            Frequency.IntervalDays -> routineWithCalendarDays.routine.intervalDays?: return emptyList()
            else -> return emptyList()
        }

        //adds the days of the week
        if(routineWithCalendarDays.routine.endDate != null){
            if(interval == null){
                //adds specific days of the week
                var date: LocalDate = startDate
                while (date <= routineWithCalendarDays.routine.endDate){
                    if(date.dayOfWeek in routineWithCalendarDays.routine.daysOfWeek!!){
                        calendarDays.add(
                            RoutineCalendarDays(
                                date = date,
                                routineId = 0,
                                isCompleted = false
                            )
                        )
                    }
                    date = date.plusDays(1)

                }
            }else{
                //adds every x days
                var date: LocalDate = startDate
                while(date <= routineWithCalendarDays.routine.endDate){
                    calendarDays.add(
                        RoutineCalendarDays(
                            date = date,
                            routineId = 0,
                            isCompleted = false
                        )
                    )
                    date = date.plusDays(interval.toLong())
                }
            }
        }else if(targetValue != null){
            //adds x times
            var date: LocalDate = startDate

            for(counter in 0..< targetValue){
                calendarDays.add(
                    RoutineCalendarDays(
                        date = date,
                        routineId = 0,
                        isCompleted = false
                    )
                )
                if(interval == null){
                    date = date.plusDays(1)
                    while(date.dayOfWeek !in routineWithCalendarDays.routine.daysOfWeek!!){
                        date = date.plusDays(1)
                    }
                }else{
                    date = date.plusDays(interval.toLong())
                }
            }
        }
        return calendarDays
    }



}