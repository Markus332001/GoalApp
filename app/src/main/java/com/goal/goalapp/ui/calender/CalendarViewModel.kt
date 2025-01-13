package com.goal.goalapp.ui.calender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goal.goalapp.TIMEOUT_MILLIS
import com.goal.goalapp.data.UserSessionStorage
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.ui.helper.CalendarDaysBackgroundColorType
import com.goal.goalapp.ui.helper.CalendarDisplay
import com.goal.goalapp.ui.helper.RoutineCalendarDayWithTitle
import com.goal.goalapp.ui.helper.getFirstDayOfWeek
import com.goal.goalapp.ui.helper.getMonthDaysForCalendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

class CalendarViewModel(
    private val goalRepository: GoalRepository,
    private val userSessionStorage: UserSessionStorage
): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val dayWithRoutineCalendarDays: StateFlow<List<CalendarDisplay>> = userSessionStorage.userIdFlow
        .filterNotNull()
        .flatMapLatest { userId ->
            goalRepository.getRoutineWithCalendarDaysByUserIdStream(userId)
        }
        .map{
            transformCalendarDaysToDates(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    /**
     * transforms all calendar days to a list of days with routine calendar days. One day can have multiple calendar days
     */
    private fun transformCalendarDaysToDates(routineCalendarDays: List<RoutineWithCalendarDays>): List<CalendarDisplay>{

        var routineCalendarDayWithTitle: MutableList<RoutineCalendarDayWithTitle> = mutableListOf()
        for(routineCalendarDay in routineCalendarDays){
            routineCalendarDay.calendarDays.forEach{
                calendarDay ->
                routineCalendarDayWithTitle.add(
                    RoutineCalendarDayWithTitle(
                        routineCalendarDays = calendarDay,
                        title = routineCalendarDay.routine.title
                    )
                )
            }
        }

        var newDayWithRoutineCalendarDays: MutableList<CalendarDisplay> = mutableListOf()
        //loops through all routine calendar days and ads them to the new list
        for(routineCalendarDay in routineCalendarDayWithTitle){
            //checks if the day already exists in the new list
            val dayWithRoutineCalendarDay = newDayWithRoutineCalendarDays.find{it.date == routineCalendarDay.routineCalendarDays.date}
            //when not it adds it to the new list
            if(dayWithRoutineCalendarDay == null){
                newDayWithRoutineCalendarDays.add(
                    CalendarDisplay(
                        date = routineCalendarDay.routineCalendarDays.date,
                        routineCalendarDays = mutableListOf(routineCalendarDay)
                    )
                )
            }else{
                dayWithRoutineCalendarDay.routineCalendarDays.add(routineCalendarDay)
            }
        }

        //loops through the new List and assigns the color of the day from the green percentage
        for(dayWithRoutineCalendarDay in newDayWithRoutineCalendarDays){
            var calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType

            //sets the color of this field depending if the day is in the future, today or in the past
            if(LocalDate.now() < dayWithRoutineCalendarDay.date) {

                calendarDaysBackgroundColorType = CalendarDaysBackgroundColorType.TasksOnThatDay
            }else if(LocalDate.now() == dayWithRoutineCalendarDay.date){

                calendarDaysBackgroundColorType = CalendarDaysBackgroundColorType.Today
            }else{

                val completedCalendarDaysCount = dayWithRoutineCalendarDay.routineCalendarDays.count{it.routineCalendarDays.isCompleted}
                val greenPercentage = completedCalendarDaysCount.toFloat() / dayWithRoutineCalendarDay.routineCalendarDays.size.toFloat()
                //assings the color of the day depending of the green percentage
                calendarDaysBackgroundColorType = if(greenPercentage == 1f) CalendarDaysBackgroundColorType.AllTasksCompleted
                else if(greenPercentage == 0f) CalendarDaysBackgroundColorType.AllTasksNotCompleted
                else CalendarDaysBackgroundColorType.NotAllTasksCompleted

                dayWithRoutineCalendarDay.greenPercentage = greenPercentage
            }

            dayWithRoutineCalendarDay.colorDaysBackgroundColorType = calendarDaysBackgroundColorType


        }

        return newDayWithRoutineCalendarDays
    }

    fun checkRoutineCalendarDay(routineCalendarDay: RoutineCalendarDays){
        updateRoutineCalendarDay(routineCalendarDay.copy(
            isCompleted = !routineCalendarDay.isCompleted
        ))
    }

    fun updateRoutineCalendarDay(routineCalendarDay: RoutineCalendarDays){
        viewModelScope.launch {
            goalRepository.updateRoutineCalendarDay(routineCalendarDay)
        }
    }

    private fun generateMonthDays(currentMonth: Int, currentYear: Int, dayWithCalendarDays: List<CalendarDisplay>): List<CalendarDisplay>{
        val monthDays = getMonthDaysForCalendar(currentMonth, currentYear).toMutableList()

        //assigns no Color to all days of the month. Later it specifies the color for specific days. Except today there it assigns an other color
        monthDays.forEach{ monthDay ->
            if(LocalDate.now() == monthDay.date) monthDay.colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.Today
            else monthDay.colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NoTasksOnThatDay
        }

        //filters for all calendar days from db of the current month
        val calendarDaysInMonth = dayWithCalendarDays.filter { it.date.monthValue == currentMonth  }

        for(calendarDay in calendarDaysInMonth){
            //searches for the day of the Month and replaces it
            val dayIndex = monthDays.indexOfFirst { it.date == calendarDay.date }
            if(dayIndex != -1){
                monthDays[dayIndex] = calendarDay
            }
        }
        return monthDays
    }

    fun getCurrentVisibleDay(date: LocalDate?): CalendarDisplay?{

        val newDate = date ?: LocalDate.now()
        //checks if the day already exists in the list from the db
        var dayWithCalendarDay = dayWithRoutineCalendarDays.value.find{
            it.date == newDate
        }
        //if its null it creates a empty one
        if(dayWithCalendarDay == null){
            dayWithCalendarDay = CalendarDisplay(
                date = newDate,
                colorDaysBackgroundColorType = CalendarDaysBackgroundColorType.NoTasksOnThatDay
            )
        }

        return dayWithCalendarDay
    }

    fun generateMonthDaysStructure(currentDate: LocalDate, dayWithCalendarDays: List<CalendarDisplay>): List<List<CalendarDisplay>>{
        val monthDays = generateMonthDays(currentDate.monthValue, currentDate.year, dayWithCalendarDays)
        val monthDaysStructure = mutableListOf<List<CalendarDisplay>>()

        for(i in monthDays.indices step 7){
            monthDaysStructure.add(monthDays.subList(i, i + 7))
        }
        return monthDaysStructure
    }

    fun calculateWeekPage(date: LocalDate, startPage: Int): Int{
        val differenceInDays = ChronoUnit.DAYS.between(LocalDate.now(), date)
        return startPage + (differenceInDays / 7).toInt()
    }

    fun calculateMonthPage(date: LocalDate, startPage: Int): Int{
        return startPage + Period.between(LocalDate.now().withDayOfMonth(1), date.withDayOfMonth(1)).months
    }



    fun generateWeekDays(currentDate: LocalDate, dayWithCalendarDays: List<CalendarDisplay>): List<CalendarDisplay>{
        val firstDayOfWeek = getFirstDayOfWeek(currentDate)

        //filters for all calendar days from db of the current week
        val calendarDaysInWeek = dayWithCalendarDays.filter{
            it.date.isAfter(firstDayOfWeek.minusDays(1)) && it.date.isBefore(firstDayOfWeek.plusDays(7))
        }
        val weekDays = mutableListOf<CalendarDisplay>()

        //generates the days of the week
        for(i in 0..6) {
            val date = firstDayOfWeek.plusDays(i.toLong())
            val calendarDay = calendarDaysInWeek.find { it.date == date }
            if (calendarDay != null) {
                weekDays.add(calendarDay)
            } else {
                weekDays.add(
                    CalendarDisplay(
                        date = date,
                        isVisible = true,
                        colorDaysBackgroundColorType = if(LocalDate.now() == date) CalendarDaysBackgroundColorType.Today
                                else CalendarDaysBackgroundColorType.NoTasksOnThatDay
                    )
                )
            }
        }

        return weekDays
    }
}
