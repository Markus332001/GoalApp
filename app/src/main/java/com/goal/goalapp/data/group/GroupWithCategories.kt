package com.goal.goalapp.data.group

import androidx.room.Embedded
import androidx.room.Relation

data class GroupWithCategories(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val categories: List<GroupCategory>
)
