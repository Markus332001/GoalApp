package com.goal.goalapp.data.post

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.BaseEntity
import com.goal.goalapp.data.CompletionType
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.user.User

// Post Entity
@Entity(
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = ForeignKey.NO_ACTION
    ),
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.NO_ACTION
    )],
    indices = [Index("groupId"), Index("userId")]
)
data class Post(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val userId: Int,
    val groupId: Int,
    val goalName: String,
    val progress: Float?,
    val completionType: CompletionType?,
    val targetValue: Int?,
    val currentValue: Int?,
    val unit: String?,
    val likesUserIds: List<Int>
): BaseEntity()