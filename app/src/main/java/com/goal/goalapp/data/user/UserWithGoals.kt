package com.goal.goalapp.data.user

import androidx.room.Embedded
import androidx.room.Relation
import com.goal.goalapp.data.goal.Goal

data class UserWithGoals (
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val goals: List<Goal>
)