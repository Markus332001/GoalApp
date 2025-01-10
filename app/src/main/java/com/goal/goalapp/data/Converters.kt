package com.goal.goalapp.data

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Converts Date to Long and vice versa. So it can be used in the database
class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }

    // transformation of List<DaysOfWeek> in String (through comma seperation)
    @TypeConverter
    fun fromDaysOfWeekList(days: List<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    // transformation of String back to List<DaysOfWeek>
    @TypeConverter
    fun toDaysOfWeekList(data: String?): List<DayOfWeek>? {
        return if(data?.isEmpty() == true) null else data?.split(",")?.map { DayOfWeek.valueOf(it) }
    }
}
