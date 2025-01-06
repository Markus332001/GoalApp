package com.goal.goalapp.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.DaysOfWeek
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

data class CreateGoal(
    val title: String = "",
    val deadline: Date = Date(),
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

    fun updateGoalCompletionCriteriaReachTargetValue(targetValue: Int, unit: String) {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(
                completionType = CompletionType.ReachTargetValue,
                targetValue = targetValue,
                unit = unit
            )
        )
    }

}


