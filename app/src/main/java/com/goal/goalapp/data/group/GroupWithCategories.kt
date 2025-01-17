package com.goal.goalapp.data.group

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GroupWithCategories(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupGroupCategoryCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "groupCategoryId"
        )
    )
    val categories: List<GroupCategory>
)
