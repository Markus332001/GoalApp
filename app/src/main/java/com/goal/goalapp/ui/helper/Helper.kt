package com.goal.goalapp.ui.helper

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goal.goalapp.R
import com.goal.goalapp.data.Frequency
import com.goal.goalapp.data.goal.GoalWithDetails
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineSummary
import com.goal.goalapp.data.goal.RoutineWithCalendarDays
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.post.Post
import com.goal.goalapp.data.post.PostWithDetails
import com.goal.goalapp.data.user.User
import com.goal.goalapp.ui.components.SelectGoalDialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

data class ColorScheme(
    val fontColor: Color,
    val backgroundColor: Color
)

data class CalendarDisplay(
    val date: LocalDate,
    var colorDaysBackgroundColorType: CalendarDaysBackgroundColorType? = null,
    var routineCalendarDays: MutableList<RoutineCalendarDayWithTitle> = mutableListOf(),
    val isVisible: Boolean = true,
    var greenPercentage: Float? = null
)

data class RoutineCalendarDayWithTitle(
    val routineCalendarDays: RoutineCalendarDays,
    val title: String
)

/**
 * Helper function to get the short form of the days of the week
 */
fun getDaysOfWeekShort(daysOfWeek: List<DayOfWeek>): List<String>{
    val daysOfWeekShort = mutableListOf<String>()
    for(day in daysOfWeek){
        daysOfWeekShort.add(getDayShortForm(day))
    }
    return daysOfWeekShort
}

fun getFirstDayOfWeek(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

fun getDayShortForm(day: DayOfWeek): String{
    return when(day){
        DayOfWeek.MONDAY -> "Mo"
        DayOfWeek.TUESDAY -> "Di"
        DayOfWeek.WEDNESDAY -> "Mi"
        DayOfWeek.THURSDAY -> "Do"
        DayOfWeek.FRIDAY -> "Fr"
        DayOfWeek.SATURDAY -> "Sa"
        DayOfWeek.SUNDAY -> "So"
    }
}

/**
 * Gets the days of the specific month in the calendar
 * Gets also the days of the last month and next month in the calendar view of the specific month
 */
fun getMonthDaysForCalendar(month: Int, year: Int): List<CalendarDisplay>{

    val firstDayOfMonth = LocalDate.of(year, month, 1)

    //gets the days from last Month in the calendar view, because it has to show the whole week
    val daysOfLastMontInCalendarView = firstDayOfMonth.dayOfWeek.value.toLong() - 1;
    val firstDayInCalendarView = firstDayOfMonth.minusDays(daysOfLastMontInCalendarView)
    val monthsDaysForCalendar: MutableList<CalendarDisplay> = mutableListOf()

    monthsDaysForCalendar.add(
        CalendarDisplay(
            date = firstDayInCalendarView,
            isVisible = month == firstDayInCalendarView.monthValue
        )
    )

    while(true){

        val nextDate = monthsDaysForCalendar.last().date.plusDays(1)
        /**
         * Checks if the it is the next month and if it is a new Week (If the day is a Monday)
         */
        if(nextDate.minusMonths(1).monthValue == month  && nextDate.dayOfWeek == DayOfWeek.MONDAY){
            break;
        }
        monthsDaysForCalendar.add(
            CalendarDisplay(
                date = nextDate,
                isVisible = month == nextDate.monthValue
            )

        )
    }

    return monthsDaysForCalendar
}





/**
 * Helper function to get the string representation of the frequency
 */
@Composable
fun getFrequencyString(frequency: Frequency, intervalDays: Int? = null, daysOfWeek: List<DayOfWeek>? = null): String{
    return when (frequency) {
        Frequency.Daily -> {
            return stringResource(R.string.daily)
        }
        Frequency.Weekly -> stringResource(R.string.weekly) + ": " +
                getDaysOfWeekShort(daysOfWeek ?: emptyList())
                    .joinToString(", ") { it }
        Frequency.IntervalDays -> stringResource(R.string.every) + " " + intervalDays.toString() + " " + stringResource(
            R.string.day
        )
    }
}

/**
 * Helper function to convert a Date to a String
 */
@SuppressLint("SimpleDateFormat")
fun convertDateToStringFormat(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return date.format(formatter)
}

/**
 * Helper function convert a Date to a String with Dots separated
 */
@SuppressLint("SimpleDateFormat")
fun convertDateToStringFormatDots(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return date.format(formatter)
}

@Composable
fun getColorFromCalendarDaysBackgroundColorType(calendarDaysBackgroundColorType: CalendarDaysBackgroundColorType): ColorScheme {
    return when(calendarDaysBackgroundColorType){
        CalendarDaysBackgroundColorType.AllTasksCompleted -> ColorScheme(
            fontColor = Color.White,
            backgroundColor = colorResource(R.color.positive)
        )
        CalendarDaysBackgroundColorType.AllTasksNotCompleted -> ColorScheme(
            fontColor = Color.White,
            backgroundColor = colorResource(R.color.negative)
        )
        CalendarDaysBackgroundColorType.Today -> ColorScheme(
            fontColor = Color.White,
            backgroundColor = colorResource(R.color.today)
        )
        CalendarDaysBackgroundColorType.TasksOnThatDay -> ColorScheme(
            fontColor = colorResource(R.color.button_font),
            backgroundColor = Color.LightGray
        )
        CalendarDaysBackgroundColorType.NoTasksOnThatDay -> ColorScheme(
            fontColor = colorResource(R.color.button_font),
            backgroundColor = Color.White
        )
        // returns the same color as all tasks completed. It also has to get all tasks not completed
        CalendarDaysBackgroundColorType.NotAllTasksCompleted -> ColorScheme(
            fontColor = Color.White,
            backgroundColor = colorResource(R.color.positive)
        )
    }
}

/**
 * get String representation of the specific Month
 */
@Composable
fun getMonthString(month: Int): String{
    return when(month){
        1 -> stringResource(R.string.janunary)
        2 -> stringResource(R.string.february)
        3 -> stringResource(R.string.march)
        4 -> stringResource(R.string.april)
        5 -> stringResource(R.string.may)
        6 -> stringResource(R.string.june)
        7 -> stringResource(R.string.july)
        8 -> stringResource(R.string.august)
        9 -> stringResource(R.string.september)
        10 -> stringResource(R.string.october)
        11 -> stringResource(R.string.november)
        12 -> stringResource(R.string.december)
        else -> ""
    }
}
@Composable
fun getStringFromFrequency(routine: Routine): String{
    return when(routine.frequency){
        Frequency.Daily -> stringResource(R.string.daily)
        Frequency.Weekly -> stringResource(R.string.weekly) + ": " + getDaysOfWeekShort(routine.daysOfWeek ?: emptyList())
        Frequency.IntervalDays -> stringResource(R.string.every) + " " + routine.intervalDays.toString() + " " + stringResource(R.string.day)
    }
}

@Composable
fun transformInfosForPost(
    goalWithDetails: GoalWithDetails,
    routines: List<Routine>,
    withProgress: Boolean,
): PostWithDetails{
    val routineSummaries = routines.map{
        RoutineSummary(
            postId = 0,
            title = it.title,
            frequency = getStringFromFrequency(it),
            progress = it.progress
        )
    }

    return PostWithDetails(
        post = Post(
            userId = 0,
            groupId = 0,
            goalName = goalWithDetails.goal.title,
            progress = if(withProgress) goalWithDetails.goal.progress else null,
            completionType = if(withProgress) goalWithDetails.completionCriteria.completionType else null,
            targetValue = if(withProgress) goalWithDetails.completionCriteria.targetValue else null,
            currentValue = if(withProgress) goalWithDetails.completionCriteria.currentValue else null,
            unit = if(withProgress) goalWithDetails.completionCriteria.unit else null,
            likesUserIds = emptyList()
        ),
        comments = emptyList(),
        routineSummary = routineSummaries,
        user = User(
            username = "",
            email = "",
            passwordHash = ""
        )
    )
}

