package com.goal.goalapp.data

import androidx.room.TypeConverter
import java.util.Date

// Converts Date to Long and vice versa. So it can be used in the database
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // transformation of List<DaysOfWeek> in String (through comma seperation)
    @TypeConverter
    fun fromDaysOfWeekList(days: List<DaysOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    // transformation of String back to List<DaysOfWeek>
    @TypeConverter
    fun toDaysOfWeekList(data: String?): List<DaysOfWeek>? {
        return data?.split(",")?.mapNotNull { DaysOfWeek.valueOf(it) }
    }
}
