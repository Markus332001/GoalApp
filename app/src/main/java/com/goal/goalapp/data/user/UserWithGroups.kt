package com.goal.goalapp.data.user

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.goal.goalapp.data.group.Group

data class UserWithGroups(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UserGroupCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "userId"
        )
    )
    val groups: List<Group>
)