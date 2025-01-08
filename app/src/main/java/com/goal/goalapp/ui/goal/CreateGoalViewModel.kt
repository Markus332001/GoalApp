package com.goal.goalapp.ui.goal

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.DaysOfWeek
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.CompletionCriterion
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.user_session.UserSession
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class CreateGoal(
    val title: String = "",
    val deadline: Date? = null,
    val notes: String = "",
    val completionCriteria: CreateCompletionCriterion? = CreateCompletionCriterion(),
    val routines: List<CreateRoutine> = emptyList()
)

data class CreateCompletionCriterion(
    val completionType: CompletionType? = null,
    val completionRate: Int? = null,
    val targetValue: Int? = null,
    val unit: String? = null
)

data class CreateRoutine(
    val title: String = "",
    val frequency: Frequency? = null,
    val daysOfWeek: List<DaysOfWeek>? = null,
    val intervalDays: Int? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val endFrequency: Int? = null
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


    fun updateGoalTitle(title: String) {
        _createGoal.value = _createGoal.value.copy(title = title)
    }

    fun updateGoalDeadline(deadline: Date) {
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
            userId = 0,
            progress = 0f
        )
        if(_createGoal.value.completionCriteria?.completionType == null){
            _createGoalState.value = CreateGoalState.Error("Ungültige Completion Criterion")
            return
        }
        val completionCriterion = CompletionCriterion(
            goalId = 0,
            completionType = _createGoal.value.completionCriteria?.completionType!!,
            targetValue = _createGoal.value.completionCriteria?.targetValue,
            unit = _createGoal.value.completionCriteria?.unit,
            currentValue = 0
        )
        val routines = mutableListOf<Routine>()
        for (routine in _createGoal.value.routines) {
            if(routine.frequency == null || routine.startDate == null){
                _createGoalState.value = CreateGoalState.Error("Ungültige Routine")
                return
            }
            routines.add(
                Routine(
                    goalId = 0,
                    title = routine.title,
                    frequency = routine.frequency,
                    daysOfWeek = routine.daysOfWeek,
                    intervalDays = routine.intervalDays,
                    startDate = routine.startDate,
                    endDate = routine.endDate,
                    endFrequency = routine.endFrequency,
                    progress = 0f
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
               routines = routines
           )
           if(goalId > 0){
               _createGoalState.value = CreateGoalState.Success
           }else{
               _createGoalState.value = CreateGoalState.Error("Fehler beim Speichern des Goals")
           }
       }
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

    fun updateRoutineFrequency(frequency: Frequency, intervalDays: Int? = null, daysOfWeek: List<DaysOfWeek>? = null) {

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

    fun updateRoutineStartDate(startDate: Date) {
        _routine.value = _routine.value.copy(startDate = startDate)

    }
    fun updateRoutineEndDate(endDate: Date?, afterDays: Int?) {
        _routine.value = _routine.value.copy(endDate = endDate, endFrequency = afterDays)
    }

}

sealed class CreateGoalState {
    data object Initial : CreateGoalState() // initial state
    data object Loading : CreateGoalState() // when the login process is loading
    data object Success : CreateGoalState()
    data class Error(val message: String) : CreateGoalState()
}


