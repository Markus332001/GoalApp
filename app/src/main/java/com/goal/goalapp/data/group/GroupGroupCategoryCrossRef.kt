package com.goal.goalapp.data.group

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["groupId", "groupCategoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GroupCategory::class,
            parentColumns = ["id"],
            childColumns = ["groupCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("groupCategoryId")]
)
data class GroupGroupCategoryCrossRef (
    val groupId: Long,
    val groupCategoryId: Long
)