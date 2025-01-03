package com.goal.goalapp.data.post

import androidx.room.Embedded
import androidx.room.Relation
import com.goal.goalapp.data.comment.Comment
import com.goal.goalapp.data.goal.RoutineSummary

// Post with Comments
data class PostWithDetails(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val comments: List<Comment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val routineSummary: List<RoutineSummary>
)