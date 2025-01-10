package com.goal.goalapp.ui.helper

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.goal.goalapp.R
import com.goal.goalapp.data.Frequency
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class ColorScheme(
    val fontColor: Color,
    val backgroundColor: Color
)

data class CalendarDisplay(
    val date: LocalDate,
    val colorDaysBackgroundColorType: CalendarDaysBackgroundColorType? = null,
    val isVisible: Boolean,
    val isToday: Boolean? = null,
    val greenPercentage: Float? = null
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
            R.string.time
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
        CalendarDaysBackgroundColorType.NotAllTasksCompleted -> ColorScheme(
            fontColor = Color.White,
            backgroundColor = colorResource(R.color.positive)
        )
    }
}

/**
 * Filters the dates, which are in a specific month and year
 */
fun filterDatesByMonthAndYear(dates: List<LocalDate>, year: Int, month: Int): List<LocalDate> {
    return dates.filter { it.year == year && it.monthValue == month }
}

/**
 * Helper function to get the dates of a specific month
 */
fun getDatesInMonth(year: Int, month: Int): List<LocalDate> {
    val yearMonth = YearMonth.of(year, month) // Erstellt ein Objekt für Jahr und Monat
    return (1..yearMonth.lengthOfMonth()).map { day ->
        LocalDate.of(year, month, day)
    }
}

/**
 * Helper function to get the dates of a specific week
 */
fun getWeekDates(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.with(DayOfWeek.MONDAY) // the Monday of the week
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
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

@Preview
@Composable
fun getMonthsDaysForCalendarPreview(){
    val monthsDaysForCalendar = getMonthDaysForCalendar(4, 2023)
    Column{
        for (day in monthsDaysForCalendar) {
            Text(
                text = convertDateToStringFormatDots(day.date) + " " + day.date.dayOfWeek
            )
        }
    }

}