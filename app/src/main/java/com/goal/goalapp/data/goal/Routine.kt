package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.DaysOfWeek
import com.goal.goalapp.data.Frequency
import java.util.Date

// Routine Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("goalId")]
)
data class Routine(
    val title: String,
    val goalId: Int, // Goal ID
    val frequency: Frequency,
    val daysOfWeek: List<DaysOfWeek>?,
    val intervalDays: Int?,
    val startDate: Date,
    val endDate: Date?,
    val endFrequency: Int?
): BaseEntity()
