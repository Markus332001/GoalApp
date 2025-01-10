package com.goal.goalapp.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.CompletionCriterion
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.Routine
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalDetailsViewModel(
    private val goalRepository: GoalRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {

    private val _goalDetailsUiState = MutableStateFlow(GoalDetailsUiState())
    val goalDetailsUiState: StateFlow<GoalDetailsUiState> = _goalDetailsUiState

    fun loadGoal(goalId: Int) {
        viewModelScope.launch {
            goalRepository.getGoalWithDetailsByIdStream(goalId)
                .filterNotNull()
                .map{
                    GoalDetailsUiState(
                        goalId = it.goal.id,
                        title = it.goal.title,
                        progress = it.goal.progress,
                        deadline = it.goal.deadline,
                        notes = it.goal.notes,
                        routines = it.routines.map{r -> r.routine},
                        completionCriteria = it.completionCriteria
                    )
                }
                .collect{
                    _goalDetailsUiState.value = it
                }
        }
    }

    fun toggleProgressReachGoal(){
        val newProgress = if(_goalDetailsUiState.value.progress >= 1) 0f else 1f
        viewModelScope.launch{
            val goalDb = goalRepository.getGoalByIdStream(_goalDetailsUiState.value.goalId).first()
            if(goalDb != null){
                val newGoalDb = goalDb.copy(progress = newProgress)
                val goalDbId = goalRepository.update(goalDb.copy(progress = newProgress, id = goalDb.id))
                println(goalDbId)
            }
        }
    }

    fun updateTargetValue(newValue: Int){
        /**
         * Checks if the new value is valid.
         */
        if(newValue < 0 || newValue > (_goalDetailsUiState.value.completionCriteria.targetValue?: 0)){
            return
        }
        viewModelScope.launch{
            val goalWithDetailsDb = goalRepository.getGoalWithDetailsByIdStream(_goalDetailsUiState.value.goalId).first()
            if(goalWithDetailsDb != null){

                /**
                 * Checks if the current value is null or the target value is null.
                 */
                if(goalWithDetailsDb.completionCriteria.currentValue == null || goalWithDetailsDb.completionCriteria.targetValue == null){
                    cancel()
                }

                /**
                 * Calculates the progress of the goal.
                 */
                val progressGoal = newValue.toFloat() / goalWithDetailsDb.completionCriteria.targetValue!!.toFloat()

                /**
                 * Updates the current value in the database.
                 */
                goalRepository.updateGoalWithDetails(goalWithDetailsDb.copy(goal = goalWithDetailsDb.goal.copy(progress = progressGoal),
                    completionCriteria = goalWithDetailsDb.completionCriteria.copy(currentValue = newValue)))
            }
        }
    }

    fun addOrSubtractTargetValue(add: Boolean){
        /**
         * Adds or subtracts 1 from the current value.
         */
        if(_goalDetailsUiState.value.completionCriteria.currentValue != null){
            val newValue = _goalDetailsUiState.value.completionCriteria.currentValue!! + if(add) + 1 else - 1
            updateTargetValue(newValue)
        }
    }

}

data class GoalDetailsUiState(
    val goalId: Int = 0,
    val title: String = "",
    val progress: Float = 0f,
    val deadline: LocalDate? = null,
    val notes: String = "",
    val routines: List<Routine> = emptyList(),
    val completionCriteria: CompletionCriterion = CompletionCriterion(
        goalId = 0,
        completionType = CompletionType.ReachGoal,
        targetValue = null,
        unit = null,
        currentValue = 0
    )
)