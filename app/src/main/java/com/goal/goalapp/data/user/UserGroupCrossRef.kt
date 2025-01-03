package com.goal.goalapp.data.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.goal.goalapp.data.group.Group

@Entity(
    primaryKeys = ["userId", "groupId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("groupId")]
)
data class UserGroupCrossRef (
    val userId: Long,
    val groupId: Long
)