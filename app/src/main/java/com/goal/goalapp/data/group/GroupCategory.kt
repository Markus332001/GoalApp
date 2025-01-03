package com.goal.goalapp.data.group

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.goal.goalapp.data.BaseEntity

// GroupCategory Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("groupId")]
)
data class GroupCategory(
    val groupId: Int,
    val name: String
): BaseEntity()