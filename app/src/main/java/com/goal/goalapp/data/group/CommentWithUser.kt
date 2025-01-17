package com.goal.goalapp.data.group

import androidx.room.Embedded
import androidx.room.Relation
import com.goal.goalapp.data.user.User

data class CommentWithUser(
    @Embedded val comment: Comment,
    @Relation(
        parentColumn = "writerId",
        entityColumn = "id"
    )
    val user: User
)