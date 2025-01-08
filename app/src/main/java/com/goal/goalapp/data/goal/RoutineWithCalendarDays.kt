package com.goal.goalapp.data.goal

import androidx.room.Embedded
import androidx.room.Relation

data class RoutineWithCalendarDays (
    @Embedded val routine: Routine,
    @Relation(
        parentColumn = "id",
        entityColumn = "routineId"
    )
    val calendarDays: List<RoutineCalendarDays>
)