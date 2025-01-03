package com.goal.goalapp.data.goal

import androidx.room.Embedded
import androidx.room.Relation

// Goal with Completion Criterion and Routines
data class GoalWithDetails(
    @Embedded val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val completionCriteria: CompletionCriterion,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val routines: List<Routine>
)
