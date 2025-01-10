package com.goal.goalapp.ui.goal

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.CompletionCriterion
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

data class CreateGoal(
    val id: Int = 0,
    val title: String = "",
    val deadline: LocalDate? = null,
    val notes: String = "",
    val completionCriteria: CreateCompletionCriterion? = CreateCompletionCriterion(),
    val routines: List<CreateRoutine> = emptyList(),
    val userId: Int = 0,
    val progress: Float = 0f
)

data class CreateCompletionCriterion(
    val id: Int = 0,
    val goalId: Int = 0,
    val completionType: CompletionType? = null,
    val targetValue: Int? = null,
    val unit: String? = null,
    val currentValue: Int = 0
)

data class CreateRoutine(
    val id: Int = 0,
    val goalId: Int = 0,
    val title: String = "",
    val frequency: Frequency? = null,
    val daysOfWeek: List<DayOfWeek>? = null,
    val intervalDays: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val targetValue: Int? = null,
    val currentValue: Int = 0,
    val progress: Float = 0f,
    val calendarDays: List<RoutineCalendarDays> = emptyList()
)

class CreateGoalViewModel(
    private val goalRepository: GoalRepository,
    private val userSessionStorage: UserSessionStorage
) : ViewModel() {

    private val _createGoal = MutableStateFlow(CreateGoal())
    val createGoal: StateFlow<CreateGoal> = _createGoal.asStateFlow()

    private val _routine = MutableStateFlow(CreateRoutine())
    val routine: StateFlow<CreateRoutine> = _routine.asStateFlow()

    private val _createGoalState = MutableStateFlow<CreateGoalState>(CreateGoalState.Initial)
    val createGoalState: StateFlow<CreateGoalState> get() = _createGoalState


    fun getGoalDetailsFromDb(goalId: Int){
        viewModelScope.launch {
            goalRepository.getGoalWithDetailsByIdStream(goalId).collect{
                if(it == null) return@collect
                _createGoal.value = CreateGoal(
                    id = it.goal.id,
                    title = it.goal.title,
                    deadline = it.goal.deadline,
                    notes = it.goal.notes,
                    userId = it.goal.userId,
                    progress = it.goal.progress,
                    completionCriteria = CreateCompletionCriterion(
                        id = it.completionCriteria.id,
                        goalId = it.completionCriteria.goalId,
                        completionType = it.completionCriteria.completionType,
                        targetValue = it.completionCriteria.targetValue,
                        unit = it.completionCriteria.unit,
                        currentValue = it.completionCriteria.currentValue?: 0,
                    ),
                    routines = it.routines.map { r ->
                        CreateRoutine(
                            id = r.routine.id,
                            goalId = r.routine.goalId,
                            title = r.routine.title,
                            frequency = r.routine.frequency,
                            daysOfWeek = r.routine.daysOfWeek,
                            intervalDays = r.routine.intervalDays,
                            startDate = r.routine.startDate,
                            endDate = r.routine.endDate,
                            targetValue = r.routine.targetValue,
                            currentValue = r.routine.currentValue?: 0,
                            progress = r.routine.progress,
                            calendarDays = r.calendarDays,
                        )
                    }

                )
            }
        }
    }

    fun updateGoalTitle(title: String) {
        _createGoal.value = _createGoal.value.copy(title = title)
    }

    fun updateGoalDeadline(deadline: LocalDate) {
        _createGoal.value = _createGoal.value.copy(deadline = deadline)
    }

    fun updateGoalNotes(notes: String) {
        _createGoal.value = _createGoal.value.copy(notes = notes)
    }

    fun updateGoalCompletionCriteriaReachGoal() {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(completionType = CompletionType.ReachGoal)
        )
    }

    fun updateGoalCompletionCriteriaConnectRoutine() {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(completionType = CompletionType.ConnectRoutine)
        )
    }

    fun resetCreateGoal() {
        _createGoal.value = CreateGoal()
        _createGoalState.value = CreateGoalState.Initial
    }

    /**
     * Saves the goal to the database and returns true if successful
     */
    @SuppressLint("SuspiciousIndentation")
    fun saveCreateGoal(){
        _createGoalState.value = CreateGoalState.Loading

        if(_createGoal.value.deadline == null){
            _createGoalState.value = CreateGoalState.Error("Ungültige Deadline")
            return
        }
        val goal = Goal(
            title = _createGoal.value.title,
            deadline = _createGoal.value.deadline!!,
            notes = _createGoal.value.notes,
            userId = _createGoal.value.userId,
            progress = _createGoal.value.progress
        )
        if(_createGoal.value.completionCriteria?.completionType == null){
            _createGoalState.value = CreateGoalState.Error("Ungültige Completion Criterion")
            return
        }
        val completionCriterion = CompletionCriterion(
            goalId = _createGoal.value.completionCriteria?.goalId!!,
            completionType = _createGoal.value.completionCriteria?.completionType!!,
            targetValue = _createGoal.value.completionCriteria?.targetValue,
            unit = _createGoal.value.completionCriteria?.unit,
            currentValue = _createGoal.value.completionCriteria?.currentValue
        )
        val routinesWithCalendarDays = mutableListOf<RoutineWithCalendarDays>()
        for (routine in _createGoal.value.routines) {
            if(routine.frequency == null || routine.startDate == null){
                _createGoalState.value = CreateGoalState.Error("Ungültige Routine")
                return
            }



            routinesWithCalendarDays.add(
                RoutineWithCalendarDays(
                    routine = Routine(
                        goalId = routine.goalId,
                        title = routine.title,
                        frequency = routine.frequency,
                        daysOfWeek = routine.daysOfWeek,
                        intervalDays = routine.intervalDays,
                        startDate = routine.startDate,
                        endDate = routine.endDate,
                        targetValue = routine.targetValue,
                        currentValue = routine.currentValue,
                        progress = routine.progress
                    ),
                    calendarDays = createOrEditRoutineCalendarDays(routine)
                )
            )
        }
        viewModelScope.launch {
            /**
             * Sets the userId
             */
           val userSession = userSessionStorage.loadLoginStatus()
            if(userSession.userId == null){
             _createGoalState.value = CreateGoalState.Error("Ungültige Benutzer ID")
                cancel()
            }else {
                goal.userId = userSession.userId
            }

            /**
             * Saves the goal to the database
             */
            val goalId = goalRepository.insertGoalWithDetails(
               goal = goal,
               completionCriteria = completionCriterion,
               routinesWithCalendarDays = routinesWithCalendarDays
           )
           if(goalId > 0){
               _createGoalState.value = CreateGoalState.Success
           }else{
               _createGoalState.value = CreateGoalState.Error("Fehler beim Speichern des Goals")
           }
       }
    }

    private fun createOrEditRoutineCalendarDays(routine: CreateRoutine): List<RoutineCalendarDays>{

        if(routine.startDate == null){
            return emptyList()
        }

        if(routine.calendarDays.isEmpty()){
            return createRoutineCalendarDays(
                routine = routine,
                startDate = routine.startDate,
                targetValue = routine.targetValue)
        }

        var newStartDate: LocalDate = routine.startDate
        if(routine.startDate < LocalDate.now()){
            newStartDate = LocalDate.now()
        }

        val areCompleted = routine.calendarDays.count { it.isCompleted }
        val newTargetValue = routine.targetValue?.minus(areCompleted)

        return createRoutineCalendarDays(
            routine = routine,
            startDate = newStartDate,
            targetValue = newTargetValue
        )
    }

    private fun createRoutineCalendarDays(
        routine: CreateRoutine,
        startDate: LocalDate,
        targetValue: Int?,
    ): List<RoutineCalendarDays>{
        var calendarDays = mutableListOf<RoutineCalendarDays>()
        calendarDays.addAll(routine.calendarDays)

        val interval = when(routine.frequency){
            Frequency.Daily -> 1
            Frequency.Weekly -> if(routine.daysOfWeek == null) return emptyList() else null
            Frequency.IntervalDays -> routine.intervalDays?: return emptyList()
            else -> return emptyList()
        }

        //adds the days of the week
        if(routine.endDate != null){
            if(interval == null){
                //adds specific days of the week
                var date: LocalDate = startDate
                while (date <= routine.endDate){
                    if(date.dayOfWeek in routine.daysOfWeek!!){
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
                while(date <= routine.endDate){
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
                    while(date.dayOfWeek !in routine.daysOfWeek!!){
                        date = date.plusDays(1)
                    }
                }else{
                    date = date.plusDays(interval.toLong())
                }
            }
        }
        return calendarDays
    }

    fun updateGoalCompletionCriteriaReachTargetValue(targetValue: Int, unit: String) {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(
                completionType = CompletionType.ReachTargetValue,
                targetValue = targetValue,
                unit = unit
            )
        )
    }

    fun addRoutine() {
        _createGoal.value = _createGoal.value.copy(
            routines = _createGoal.value.routines.toMutableList().apply {
                add(_routine.value.copy())
            }
        )
        _routine.value = CreateRoutine()
    }

    fun updateRoutineTitle(title: String) {
        _routine.value = _routine.value.copy(title = title)
    }

    fun updateRoutineFrequency(frequency: Frequency, intervalDays: Int? = null, daysOfWeek: List<DayOfWeek>? = null) {

        if(
            frequency == Frequency.IntervalDays
            ){
            _routine.value = _routine.value.copy(frequency = frequency, daysOfWeek = emptyList(), intervalDays = intervalDays)
        }else if(
            frequency == Frequency.Weekly &&
            daysOfWeek?.isNotEmpty() == true
        ){
            _routine.value = _routine.value.copy(frequency = frequency, daysOfWeek = daysOfWeek, intervalDays = null)
        }else if(
            frequency == Frequency.Daily
        ){
            _routine.value = _routine.value.copy(frequency = frequency, daysOfWeek = emptyList(), intervalDays = null)
        }
    }

    fun updateRoutineStartDate(startDate: LocalDate) {
        _routine.value = _routine.value.copy(startDate = startDate)

    }
    fun updateRoutineEndDate(endDate: LocalDate?, afterDays: Int?) {
        _routine.value = _routine.value.copy(endDate = endDate, targetValue = afterDays)
    }

}

sealed class CreateGoalState {
    data object Initial : CreateGoalState() // initial state
    data object Loading : CreateGoalState() // when the login process is loading
    data object Success : CreateGoalState()
    data class Error(val message: String) : CreateGoalState()
}


