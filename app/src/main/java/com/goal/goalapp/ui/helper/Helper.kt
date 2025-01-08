package com.goal.goalapp.ui.helper

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.goal.goalapp.data.DaysOfWeek
import java.util.Date

/**
 * Helper function to get the short form of the days of the week
 */
fun getDaysOfWeekShort(daysOfWeek: List<DaysOfWeek>): List<String>{
    val daysOfWeekShort = mutableListOf<String>()
    for(day in daysOfWeek){
        daysOfWeekShort.add(getDayShortForm(day))
    }
    return daysOfWeekShort
}

fun getDayShortForm(day: DaysOfWeek): String{
    return when(day){
        DaysOfWeek.Monday -> "Mo"
        DaysOfWeek.Tuesday -> "Di"
        DaysOfWeek.Wednesday -> "Mi"
        DaysOfWeek.Thursday -> "Do"
        DaysOfWeek.Friday -> "Fr"
        DaysOfWeek.Saturday -> "Sa"
        DaysOfWeek.Sunday -> "So"
    }
}

/**
 * Helper function to convert a Date to a String
 */
@SuppressLint("SimpleDateFormat")
fun convertDateToStringFormat(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(date)
}

/**
 * Helper function convert a Date to a String with Dots separated
 */
@SuppressLint("SimpleDateFormat")
fun convertDateToStringFormatDots(date: Date): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy")
    return formatter.format(date)
}