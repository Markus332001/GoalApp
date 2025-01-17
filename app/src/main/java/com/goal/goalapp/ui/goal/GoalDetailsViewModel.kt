package com.goal.goalapp.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.CompletionCriterion
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.post.PostRepository
import com.goal.goalapp.data.post.PostWithDetails
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalDetailsViewModel(
    private val goalRepository: GoalRepository,
    private val userSessionStorage: UserSessionStorage,
    private val postRepository: PostRepository,
    private val groupRepository: GroupRepository
): ViewModel() {

    private val _goalWithDetails = MutableStateFlow(GoalWithDetails(
        goal = Goal(
            id = 0,
            title = "",
            progress = 0f,
            deadline = LocalDate.now(),
            notes = "",
            userId = 0
        ),
        routines = emptyList(),
        completionCriteria = CompletionCriterion(
            id = 0,
            completionType = CompletionType.ReachGoal,
            targetValue = 0,
            currentValue = 0,
            unit = "",
            goalId = 0
        )
    ))
    val goalWithDetails: StateFlow<GoalWithDetails> = _goalWithDetails

    val userId:StateFlow<Int> = userSessionStorage.userIdFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )


    fun loadGoal(goalId: Int) {
        viewModelScope.launch {
            goalRepository.getGoalWithDetailsByIdStream(goalId)
                .filterNotNull()
                .collect{
                    _goalWithDetails.value = it
                }
        }
    }

    fun toggleProgressReachGoal(){
        val newProgress = if(_goalWithDetails.value.goal.progress >= 1) 0f else 1f
        viewModelScope.launch{
            val goalDb = goalRepository.getGoalByIdStream(_goalWithDetails.value.goal.id).first()
            if(goalDb != null){
                val goalDbId = goalRepository.update(goalDb.copy(progress = newProgress, id = goalDb.id))
                println(goalDbId)
            }
        }
    }

    fun updateTargetValue(newValue: Int){
        /**
         * Checks if the new value is valid.
         */
        if(newValue < 0 || newValue > (_goalWithDetails.value.completionCriteria.targetValue?: 0)){
            return
        }
        viewModelScope.launch{
            val goalWithDetailsDb = goalRepository.getGoalWithDetailsByIdStream(_goalWithDetails.value.goal.id).first()
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

    fun addOrSubtractCurrentValue(add: Boolean){
        /**
         * Adds or subtracts 1 from the current value.
         */
        val newValue = _goalWithDetails.value.completionCriteria.currentValue!! + if(add) + 1 else - 1
        updateTargetValue(newValue)
    }

    fun addPostWithDetailsDb(postWithDetails: PostWithDetails, groups: List<Group>){
        if(groups.isEmpty()) return
        viewModelScope.launch {
            postRepository.insertPostWithDetailsToGroupsId(
                postWithDetails.copy(post = postWithDetails.post.copy(userId = userId.value)),
                groups.map { it.id }
            )
        }
    }

    suspend fun getAllGroupsFromUser(): List<Group> {
        return groupRepository.getGroupsByUserId(userId.value.toLong())
    }

}