package com.goal.goalapp.ui.goal

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.ui.helper.CalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.CalendarDisplay
import com.goal.goalapp.ui.helper.getMonthDaysForCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class RoutineDetailsViewModel(
    private val goalRepository: GoalRepository
): ViewModel() {

    private val _calendarStructure = mutableStateOf<SnapshotStateList<SnapshotStateList<CalendarDisplay>>>(
        mutableStateListOf()
    )
    val calendarStructure: State<SnapshotStateList<SnapshotStateList<CalendarDisplay>>> = _calendarStructure

    private val _calendarYearMonth = mutableStateOf(YearMonth.now())
    val calendarYearMonth: State<YearMonth> = _calendarYearMonth

    private val _routine = MutableStateFlow<RoutineWithCalendarDays?>(null)
    val routine: StateFlow<RoutineWithCalendarDays?> = _routine

    /**
     * Load routine with calendar days from the database
     */
    fun loadRoutineWithCalendarDays(routineId: Int){
        viewModelScope.launch {
            goalRepository.getRoutineWithCalendarDaysByIdStream(routineId)
                .filterNotNull()
                .collect {
                    _routine.value = it
                }
        }
    }

    /**
     * Get color of the day in Calendar
     */
    fun getCalendarDayColor(
        date: LocalDate,
        daysOfRoutineInMonth: List<RoutineCalendarDays>
    ): CalendarDaysBackgroundColorType {
        if(date == LocalDate.now()){
            return CalendarDaysBackgroundColorType.Today
        }
        val dayOfRoutineAtDate = daysOfRoutineInMonth.firstOrNull { it.date == date }

        /**
         * When the day in the routine is in the past
         */
        if(date < LocalDate.now()){
            if(dayOfRoutineAtDate != null){
                return if(dayOfRoutineAtDate.isCompleted) CalendarDaysBackgroundColorType.AllTasksCompleted
                else CalendarDaysBackgroundColorType.AllTasksNotCompleted
            }
        }
        else{
            /**
             * When the day in the routine is in the future and there is a task
             */
            if(dayOfRoutineAtDate != null){
                return CalendarDaysBackgroundColorType.TasksOnThatDay
            }
        }

        /**
         * When there is no task on that day
         */
        return CalendarDaysBackgroundColorType.NoTasksOnThatDay
    }


    /**
     * Sets the structure of the calendar
     */
    fun setCalendarStructure(){
        if(
            routine.value != null
        ) {
            //clear calendar Structure
            _calendarStructure.value = mutableStateListOf()

            val allDaysOfCalendarMonth = getMonthDaysForCalendar(
                calendarYearMonth.value.monthValue,
                calendarYearMonth.value.year
            )
            //filters for the days of the specific month
            val daysOfRoutineInMonth: List<RoutineCalendarDays> = routine.value!!.calendarDays
                .filter { it.date.monthValue == calendarYearMonth.value.monthValue }

            for((index, day) in allDaysOfCalendarMonth.withIndex()){
                //add every first day of the week a new List
                if(index % 7 == 0){
                    _calendarStructure.value.add(SnapshotStateList())
                }

                //adds to the last List the day
                _calendarStructure.value.last() += CalendarDisplay(
                    date = day.date,
                    colorDaysBackgroundColorType = getCalendarDayColor(day.date, daysOfRoutineInMonth),
                    isVisible = day.isVisible
                )
            }
        }
    }

    fun addMonthCalendarYearMonth(){
        _calendarYearMonth.value = _calendarYearMonth.value.plusMonths(1)
        setCalendarStructure()
    }

    fun substractMonthCalendarYearMonth(){
        _calendarYearMonth.value = _calendarYearMonth.value.minusMonths(1)
        setCalendarStructure()
    }
}