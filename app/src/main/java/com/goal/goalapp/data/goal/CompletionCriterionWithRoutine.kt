package com.goal.goalapp.data.goal

import androidx.room.Embedded
import androidx.room.Relation

data class CompletionCriterionWithRoutine (
    @Embedded val completionCriterion: CompletionCriterion,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val routines: List<Routine>

)