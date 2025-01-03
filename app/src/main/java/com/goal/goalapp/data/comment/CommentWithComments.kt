package com.goal.goalapp.data.comment

import androidx.room.Embedded
import androidx.room.Relation

data class CommentWithComments (
    @Embedded val comment: Comment,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentCommentId"
    )
    val comments: List<Comment>
)