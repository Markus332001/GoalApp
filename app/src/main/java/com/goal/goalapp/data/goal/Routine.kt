package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.Frequency
import java.time.DayOfWeek
import java.time.LocalDate

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
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val goalId: Int, // Goal ID
    val progress: Float,
    val frequency: Frequency,
    val daysOfWeek: List<DayOfWeek>?,
    val intervalDays: Int?,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val targetValue: Int?,
    val currentValue: Int?
): BaseEntity()
