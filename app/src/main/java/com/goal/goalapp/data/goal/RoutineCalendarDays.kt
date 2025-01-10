package com.goal.goalapp.data.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import java.time.LocalDate

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
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val date: LocalDate,
    val isCompleted: Boolean,
    val routineId: Int
): BaseEntity()