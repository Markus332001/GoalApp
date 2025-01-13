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
import com.goal.goalapp.data.goal.GoalWithDetails
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
import java.util.UUID

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
    val idEditNewRoutine: UUID = UUID.randomUUID(),
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

    private val _createEditState = MutableStateFlow<CreateEditState>(CreateEditState.Initial)
    val createEditState: StateFlow<CreateEditState> get() = _createEditState

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

    fun getRoutineDetailsFromDb(routineId: Int){
        viewModelScope.launch {
            goalRepository.getRoutineWithCalendarDaysByIdStream(routineId).collect{
                if(it == null) return@collect
                _routine.value = CreateRoutine(
                    id = it.routine.id,
                    goalId = it.routine.goalId,
                    title = it.routine.title,
                    frequency = it.routine.frequency,
                    daysOfWeek = it.routine.daysOfWeek,
                    intervalDays = it.routine.intervalDays,
                    startDate = it.routine.startDate,
                    endDate = it.routine.endDate,
                    targetValue = it.routine.targetValue,
                    currentValue = it.routine.currentValue?: 0,
                    progress = it.routine.progress,
                    calendarDays = it.calendarDays
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
            completionCriteria =  CreateCompletionCriterion(
                id = _createGoal.value.completionCriteria?.id?: 0,
                goalId = _createGoal.value.completionCriteria?.goalId?: 0,
                completionType = CompletionType.ReachGoal
            )
        )
    }

    fun updateGoalCompletionCriteriaConnectRoutine() {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(
                id = _createGoal.value.completionCriteria?.id?: 0,
                goalId = _createGoal.value.completionCriteria?.goalId?: 0,
                completionType = CompletionType.ConnectRoutine)
        )
    }

    fun deleteRoutine(){
        if(_routine.value.id != 0){
            viewModelScope.launch {
                goalRepository.deleteRoutineById(_routine.value.id)
            }
        }
    }

    fun deleteGoal(){
        if(_createGoal.value.id != 0){
            viewModelScope.launch {
                goalRepository.deleteGoalById(_createGoal.value.id)
            }
        }
    }

    fun removeRoutineFromGoal(){
        _createGoal.value = _createGoal.value.copy(
            routines = _createGoal.value.routines.filter {
                it.idEditNewRoutine == _routine.value.idEditNewRoutine
            }
        )
    }

    fun isRoutineInGoal(): Boolean{
        return _createGoal.value.routines.any { it.idEditNewRoutine ==  _routine.value.idEditNewRoutine}
    }

    fun resetCreateGoal() {
        _createGoal.value = CreateGoal()
        _createEditState.value = CreateEditState.Initial
    }

    fun resetCreateRoutine() {
        _routine.value = CreateRoutine()
        _createEditState.value = CreateEditState.Initial
    }

    /**
     * Saves the goal to the database and returns true if successful
     */
    @SuppressLint("SuspiciousIndentation")
    fun saveCreateGoal(){
        _createEditState.value = CreateEditState.Loading

        val goal = prepareGoalDb(_createGoal.value)

        if(_createGoal.value.completionCriteria == null){
            _createEditState.value = CreateEditState.Error("Ungültige Completion Criterion")
            return
        }
        val completionCriterion = prepareCompletionCriteriaDb(_createGoal.value.completionCriteria!!)
        val routinesWithCalendarDays = prepareRoutinesDb(_createGoal.value.routines)

        if(goal == null || completionCriterion == null || routinesWithCalendarDays == null){
            _createEditState.value = CreateEditState.Error("Ungültige Daten")
            return
        }

        viewModelScope.launch {
            /**
             * Sets the userId
             */
           val userSession = userSessionStorage.loadLoginStatus()
            if(userSession.userId == null){
             _createEditState.value = CreateEditState.Error("Ungültige Benutzer ID")
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
               _createEditState.value = CreateEditState.Success
           }else{
               _createEditState.value = CreateEditState.Error("Fehler beim Speichern des Goals")
           }
       }
    }

    /**
     * Prepare Goal for Db
     */

    private fun prepareGoalDb(
        createGoal: CreateGoal
    ): Goal?{
        if(createGoal.deadline == null){
            _createEditState.value = CreateEditState.Error("Ungültige Deadline")
            return null
        }
        return Goal(
            id = createGoal.id,
            title = createGoal.title,
            deadline = createGoal.deadline,
            notes = createGoal.notes,
            userId = createGoal.userId,
            progress = createGoal.progress
        )
    }

    private fun prepareRoutineDb(
        createRoutine: CreateRoutine
    ): RoutineWithCalendarDays? {
        if(createRoutine.frequency == null || createRoutine.startDate == null){
            _createEditState.value = CreateEditState.Error("Ungültige Routine")
            return null
        }

        return RoutineWithCalendarDays(
                routine = Routine(
                    id = createRoutine.id,
                    goalId = createRoutine.goalId,
                    title = createRoutine.title,
                    frequency = createRoutine.frequency,
                    daysOfWeek = createRoutine.daysOfWeek,
                    intervalDays = createRoutine.intervalDays,
                    startDate = createRoutine.startDate,
                    endDate = createRoutine.endDate,
                    targetValue = createRoutine.targetValue,
                    currentValue = createRoutine.currentValue,
                    progress = createRoutine.progress
                ),
                calendarDays = createRoutine.calendarDays
            )
    }

    /**
     * prepare routine List for Db
     */
    private fun prepareRoutinesDb(
        routinesWithCalendarDays: List<CreateRoutine>
    ): List<RoutineWithCalendarDays>?{
        val routinesWithCalendarDaysDb = mutableListOf<RoutineWithCalendarDays>()
        for (routine in routinesWithCalendarDays) {
            routinesWithCalendarDaysDb.add(
                prepareRoutineDb(routine)?: return null
            )
        }
        return routinesWithCalendarDaysDb
    }

    fun toEditRoutineScreen(createRoutine: CreateRoutine){
        _routine.value = createRoutine
    }

    /**
     * prepare completion criteria for Db
     */
    private fun prepareCompletionCriteriaDb(
        completionCriteria: CreateCompletionCriterion
    ): CompletionCriterion?{
        if(completionCriteria.completionType == null){
            _createEditState.value = CreateEditState.Error("Ungültige Completion Criterion")
            return null
        }
        return CompletionCriterion(
            id = completionCriteria.id,
            goalId = completionCriteria.goalId,
            completionType = completionCriteria.completionType,
            targetValue = completionCriteria.targetValue,
            unit = completionCriteria.unit,
            currentValue = completionCriteria.currentValue
        )
    }



    /**
     * Save or Update Goal
     */
    fun saveOrEditGoal() {
        //checks if the goal has already an Id, when yes -> edit, when no -> save
        if(_createGoal.value.id > 0){
            editGoal()
        }else{
            saveCreateGoal()
        }
    }

    /**
     * Edit Goal
     */
    private fun editGoal() {
        _createEditState.value = CreateEditState.Loading

        val goal = prepareGoalDb(_createGoal.value)

        if(_createGoal.value.completionCriteria == null){
            _createEditState.value = CreateEditState.Error("Ungültige Completion Criterion")
            return
        }
        val completionCriterion = prepareCompletionCriteriaDb(_createGoal.value.completionCriteria!!)
        val routinesWithCalendarDays = prepareRoutinesDb(_createGoal.value.routines)

        if(goal == null || completionCriterion == null || routinesWithCalendarDays == null){
            _createEditState.value = CreateEditState.Error("Ungültige Daten")
            return
        }
        viewModelScope.launch {

            /**
             * Updates the Goal with Details in the Database
             */
            val goalId = goalRepository.updateGoalWithDetails(
                GoalWithDetails(
                    goal = goal,
                    completionCriteria = completionCriterion,
                    routines = routinesWithCalendarDays
                )
            )
            if(goalId > 0){
                _createEditState.value = CreateEditState.Success
            }else{
                _createEditState.value = CreateEditState.Error("Fehler beim Updaten des Goals")
            }
        }

    }

    fun addOrEditRoutine(){
        if(routine.value.id == 0 || _createGoal.value.id != 0){
            addRoutine()
        }else{
            updateRoutine()
        }
    }

    fun updateRoutine(){
        val routineWithCalendarDaysDb = prepareRoutineDb(_routine.value)
        if(routineWithCalendarDaysDb == null){
            _createEditState.value = CreateEditState.Error("Ungültige Daten bei Routine")
            return
        }
        _routine.value = CreateRoutine()

        viewModelScope.launch {
            val routineId = goalRepository.updateRoutineWithCalendarDays(routineWithCalendarDaysDb)
            if(routineId > 0){
                _createEditState.value = CreateEditState.Success
            }else{
                _createEditState.value = CreateEditState.Error("Fehler beim Speichern der Routine")
            }
        }

    }



    fun updateGoalCompletionCriteriaReachTargetValue(targetValue: Int, unit: String) {
        _createGoal.value = _createGoal.value.copy(
            completionCriteria =  CreateCompletionCriterion(
                id = _createGoal.value.completionCriteria?.id?: 0,
                goalId = _createGoal.value.completionCriteria?.goalId?: 0,
                completionType = CompletionType.ReachTargetValue,
                targetValue = targetValue,
                unit = unit
            )
        )
    }

    fun addRoutine() {
        //removes the old routine and adds the new one
        _createGoal.value = _createGoal.value.copy(
            routines = _createGoal.value.routines.filter {
                it.idEditNewRoutine == _routine.value.idEditNewRoutine
            }.
            toMutableList().apply {
                add(_routine.value.copy())
            }
        )
        _routine.value = CreateRoutine()
        _createEditState.value = CreateEditState.Success
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

sealed class CreateEditState {
    data object Initial : CreateEditState() // initial state
    data object Loading : CreateEditState() // when the login process is loading
    data object Success : CreateEditState()
    data class Error(val message: String) : CreateEditState()
}


