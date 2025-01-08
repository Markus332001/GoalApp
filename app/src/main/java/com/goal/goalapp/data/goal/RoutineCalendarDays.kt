package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.goal.goalapp.data.BaseEntity
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("routineId")]
)
data class RoutineCalendarDays(
    val date: Date,
    val isCompleted: Boolean,
    val routineId: Int
): BaseEntity()